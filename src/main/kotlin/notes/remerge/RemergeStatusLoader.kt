package notes.remerge

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.data.util.VcsCommitsDataLoader

private val LOG = logger<RemergeStatusLoader>()

internal class RemergeStatusLoader(private val project: Project) : VcsCommitsDataLoader<RemergeStatus> {
    @Volatile
    private var disposed = false

    @Volatile
    private var wanted: Set<CommitId> = emptySet()

    override fun loadData(commits: List<CommitId>, onChange: (Map<CommitId, RemergeStatus>) -> Unit) {
        if (commits.isEmpty()) return
        wanted = commits.toSet()
        commits.groupBy(CommitId::getRoot).forEach { (root, rootCommits) ->
            report(rootCommits.associateWith { RemergeStatus(RemergeOutcome.SCHEDULED, commitId = it) }, onChange)
            remergeStatusExecutor.execute {
                if (disposed || rootCommits.none { it in wanted }) return@execute
                report(rootCommits.associateWith { RemergeStatus(RemergeOutcome.UPDATING, commitId = it) }, onChange)
                val triaged = runCatching { triage(project, root, rootCommits) }
                    .onFailure { LOG.warn("Failed to triage ${rootCommits.size} commit(s) for remerge status", it) }
                    .getOrDefault(knownStatusesOnly(rootCommits) to emptyList())
                report(triaged.first, onChange)
                triaged.second.forEach { work ->
                    report(mapOf(work.commitId to RemergeStatus(RemergeOutcome.SCHEDULED, work.originalHash, work.commitId)), onChange)
                    remergeStatusExecutor.execute {
                        if (disposed || work.commitId !in wanted) return@execute
                        report(mapOf(work.commitId to RemergeStatus(RemergeOutcome.UPDATING, work.originalHash, work.commitId)), onChange)
                        report(mapOf(work.commitId to resolve(project, root, work)), onChange)
                    }
                }
            }
        }
    }

    override fun dispose() {
        disposed = true
    }

    private fun report(result: Map<CommitId, RemergeStatus>, onChange: (Map<CommitId, RemergeStatus>) -> Unit) {
        if (result.isEmpty() || disposed) return
        ApplicationManager.getApplication().invokeLater { if (!disposed) onChange(result) }
    }
}

private fun knownStatusesOnly(commits: List<CommitId>): Map<CommitId, RemergeStatus> =
    commits.mapNotNull { commitId -> RemergeStatusCache.get(commitId)?.let { commitId to it } }.toMap()

private class PendingCherryPick(val commitId: CommitId, val originalHash: String, val tree: String)

private fun triage(project: Project, root: VirtualFile, commits: List<CommitId>): Pair<Map<CommitId, RemergeStatus>, List<PendingCherryPick>> {
    val info = runCatching { fetchCommitInfo(project, root, commits.map { it.hash.asString() }) }.getOrNull()
    val quick = LinkedHashMap<CommitId, RemergeStatus>()
    val pending = mutableListOf<PendingCherryPick>()
    for (commitId in commits) {
        if (info == null) {
            RemergeStatusCache.get(commitId)?.let { quick[commitId] = it }
            continue
        }
        val commitInfo = info[commitId.hash.asString()]
        val originalHash = commitInfo?.let { parseCherryPickSource(it.message) }
        if (commitInfo == null || originalHash == null) {
            quick[commitId] = RemergeStatus.NOT_CHERRY_PICK
        } else {
            pending.add(PendingCherryPick(commitId, originalHash, commitInfo.tree))
        }
    }
    return quick to pending
}

private fun resolve(project: Project, root: VirtualFile, work: PendingCherryPick): RemergeStatus {
    val outcome = runCatching {
        computeRemergeOutcomeGivenTree(project, root, work.commitId.hash.asString(), work.tree, work.originalHash)
    }.getOrNull()
    if (outcome == null) {
        return RemergeStatusCache.get(work.commitId) ?: RemergeStatus(RemergeOutcome.UNKNOWN, work.originalHash, work.commitId)
    }
    val status = RemergeStatus(outcome, work.originalHash, work.commitId)
    RemergeStatusCache.put(work.commitId, status)
    return status
}
