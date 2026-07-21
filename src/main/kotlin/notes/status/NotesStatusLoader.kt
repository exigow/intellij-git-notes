package notes.status

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.data.util.VcsCommitsDataLoader
import notes.NotesService

internal class NotesStatusLoader(private val project: Project) : VcsCommitsDataLoader<NotesStatus> {
    private val service get() = project.service<NotesService>()

    @Volatile
    private var disposed = false

    private var lastCommits: List<CommitId> = emptyList()
    private var lastOnChange: ((Map<CommitId, NotesStatus>) -> Unit)? = null

    init {
        service.addChangeListener(this) { reload() }
    }

    override fun loadData(commits: List<CommitId>, onChange: (Map<CommitId, NotesStatus>) -> Unit) {
        lastCommits = commits
        lastOnChange = onChange
        load(commits, onChange)
    }

    private fun reload() {
        lastOnChange?.let { load(lastCommits, it) }
    }

    private fun load(commits: List<CommitId>, onChange: (Map<CommitId, NotesStatus>) -> Unit) {
        if (disposed || commits.isEmpty()) return
        commits.groupBy(CommitId::getRoot).forEach { (root: VirtualFile, rootCommits) ->
            service.requestIndex(root) { rootIndex ->
                if (disposed) return@requestIndex
                val result = rootCommits.associateWith { NotesStatus(rootIndex[it.hash.asString()].orEmpty(), it) }
                ApplicationManager.getApplication().invokeLater { if (!disposed) onChange(result) }
            }
        }
    }

    override fun dispose() {
        disposed = true
        lastOnChange = null
    }
}
