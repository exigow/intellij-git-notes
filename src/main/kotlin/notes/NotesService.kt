package notes

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.vcs.log.CommitId
import git4idea.config.GitExecutableManager
import git4idea.repo.GitRepositoryManager
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

@Service(Service.Level.PROJECT)
internal class NotesService(
    private val project: Project
) : Disposable {
    private val index = ConcurrentHashMap<String, Map<String, List<String>>>()
    private val indexRequested = ConcurrentHashMap.newKeySet<String>()
    private val textCache = ConcurrentHashMap<CommitId, List<Note>>()
    private val textRequested = ConcurrentHashMap.newKeySet<CommitId>()
    private val refsCache = ConcurrentHashMap<String, List<String>>()
    private val indexExecutor = AppExecutorUtil.createBoundedApplicationPoolExecutor("GitNotes.Index", 1)
    private val opExecutor = AppExecutorUtil.createBoundedApplicationPoolExecutor("GitNotes.Ops", 1)

    @Volatile
    private var repaintCallback: (() -> Unit)? = null

    init {
        project.messageBus.connect(this).subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: List<VFileEvent>) {
                    val affected = events.filter { it.path.affectsNotes() }
                    if (affected.isNotEmpty()) {
                        invalidate()
                    }
                }
            },
        )
    }

    fun setRepaintCallback(callback: () -> Unit) {
        repaintCallback = callback
    }

    fun getStatus(commitId: CommitId): List<Note> {
        val rootIndex = index[commitId.root.path]
        if (rootIndex == null) {
            scheduleIndexLoad(commitId.root)
            return emptyList()
        }
        val topics = rootIndex[commitId.hash.asString()] ?: return emptyList()
        return topics.map { Note(it, "") }
    }

    fun getNotes(commitId: CommitId, onReady: ((List<Note>) -> Unit)? = null): List<Note> {
        textCache[commitId]?.let { return it }
        if (textRequested.add(commitId)) {
            opExecutor.execute {
                val notes = readNotes(commitId.root, commitId.hash.asString())
                textCache[commitId] = notes
                textRequested.remove(commitId)
                fireChanged()
                onReady?.let { cb -> ApplicationManager.getApplication().invokeLater { cb(notes) } }
            }
        }
        return getStatus(commitId)
    }

    fun addNote(
        commitId: CommitId,
        topic: String,
        text: String,
        force: Boolean = false,
        onDone: ((Boolean) -> Unit)? = null,
    ) {
        opExecutor.execute {
            val args = buildList {
                add("notes"); add("--ref=refs/notes/$topic"); add("add")
                if (force) add("-f")
                add("-m"); add(text); add(commitId.hash.asString())
            }
            val ok = runGit(commitId.root, *args.toTypedArray()) != null
            invalidate()
            onDone?.let { cb -> ApplicationManager.getApplication().invokeLater { cb(ok) } }
        }
    }

    fun deleteNote(
        commitId: CommitId,
        topic: String,
        onDone: ((Boolean) -> Unit)? = null,
    ) {
        opExecutor.execute {
            val ok = runGit(
                commitId.root,
                "notes", "--ref=refs/notes/$topic", "remove", commitId.hash.asString(),
            ) != null
            invalidate()
            onDone?.let { cb -> ApplicationManager.getApplication().invokeLater { cb(ok) } }
        }
    }

    fun getAllTopics(root: VirtualFile): Set<String> =
        notesRefs(root).map { it.removePrefix("refs/notes/") }.sorted().toSet()

    fun matchesTopics(root: VirtualFile, hash: String, topics: Set<String>): Boolean {
        if (topics.isEmpty()) return true
        val rootIndex = index[root.path] ?: loadIndexSync(root)
        val commitTopics = rootIndex[hash] ?: return false
        return commitTopics.any { it in topics }
    }

    fun loadNotes(commitId: CommitId, onReady: (List<Note>) -> Unit) {
        textCache[commitId]?.let {
            ApplicationManager.getApplication().invokeLater { onReady(it) }
            return
        }
        getNotes(commitId, onReady)
    }

    private fun scheduleIndexLoad(root: VirtualFile) {
        if (indexRequested.add(root.path)) {
            indexExecutor.execute { loadIndex(root) }
        }
    }

    private fun loadIndex(root: VirtualFile) {
        val acc = HashMap<String, MutableList<String>>()
        for (ref in notesRefs(root)) {
            val topic = ref.removePrefix("refs/notes/")
            val output = runGit(root, "notes", "--ref=$ref", "list") ?: continue
            output.lineSequence().forEach { line ->
                val commit = line.trim().substringAfter(' ', "").trim()
                if (commit.isNotEmpty()) acc.getOrPut(commit) { mutableListOf() }.add(topic)
            }
            index[root.path] = acc.mapValues { it.value.toList() }
            fireChanged()
        }
        index[root.path] = acc.mapValues { it.value.toList() }
        fireChanged()
    }

    private fun loadIndexSync(root: VirtualFile): Map<String, List<String>> {
        val acc = HashMap<String, MutableList<String>>()
        for (ref in notesRefs(root)) {
            val topic = ref.removePrefix("refs/notes/")
            val output = runGit(root, "notes", "--ref=$ref", "list") ?: continue
            output.lineSequence().forEach { line ->
                val commit = line.trim().substringAfter(' ', "").trim()
                if (commit.isNotEmpty()) acc.getOrPut(commit) { mutableListOf() }.add(topic)
            }
        }
        val snapshot = acc.mapValues { it.value.toList() }
        index[root.path] = snapshot
        indexRequested.add(root.path)
        return snapshot
    }

    private fun invalidate() {
        index.clear()
        indexRequested.clear()
        textCache.clear()
        textRequested.clear()
        refsCache.clear()
        fireChanged()
    }

    private fun fireChanged() {
        val repaint = repaintCallback
        ApplicationManager.getApplication().invokeLater {
            repaint?.invoke()
        }
    }

    private fun notesRefs(root: VirtualFile): List<String> =
        refsCache.getOrPut(root.path) {
            val output = runGit(root, "for-each-ref", "--format=%(refname)", "refs/notes") ?: return@getOrPut emptyList()
            output.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
        }

    private fun readNotes(root: VirtualFile, hash: String): List<Note> =
        notesRefs(root).mapNotNull { ref ->
            val topic = ref.removePrefix("refs/notes/")
            runGit(root, "notes", "--ref=$ref", "show", hash)
                ?.removeSuffix("\n")
                ?.takeIf { it.isNotEmpty() }
                ?.let { Note(topic, it) }
        }

    private fun String.affectsNotes(): Boolean {
        if (!contains("/refs/notes") && !endsWith("/packed-refs")) return false
        return GitRepositoryManager.getInstance(project).repositories.any { repo ->
            startsWith(repo.root.path)
        }
    }

    override fun dispose() {
        indexExecutor.shutdownNow()
        opExecutor.shutdownNow()
        index.clear()
        indexRequested.clear()
        textCache.clear()
        textRequested.clear()
        refsCache.clear()
        repaintCallback = null
    }

    private fun runGit(root: VirtualFile, vararg args: String): String? {
        val git = GitExecutableManager.getInstance().getPathToGit(project)
        LOG.warn("git ${args.joinToString(" ")}")
        val command = GeneralCommandLine(git, *args)
            .withWorkDirectory(root.path)
            .withCharset(StandardCharsets.UTF_8)
        return try {
            val output = ExecUtil.execAndGetOutput(command, 10_000)
            if (output.exitCode != 0) null else output.stdout
        } catch (_: Exception) {
            null
        }
    }

    private companion object {
        private val LOG = logger<NotesService>()
    }
}
