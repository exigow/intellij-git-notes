package notes.remerge

import com.intellij.diff.DiffDialogHints
import com.intellij.diff.DiffManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vcs.changes.actions.diff.ChangeDiffRequestProducer
import com.intellij.openapi.vcs.changes.ui.ChangeDiffRequestChain
import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.VcsLogDataKeys
import notes.MessageBundle
import notes.action.getSelectedCommitId

class RemergeDiffAction : AnAction() {
    init {
        templatePresentation.text = MessageBundle.message("notes.remergeDiff")
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.isWorthInspecting()
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val commitId = e.getSelectedCommitId() ?: return
        val originalHash = e.cherryPickSource() ?: return
        performRemergeDiff(project, commitId, originalHash)
    }

    private fun AnActionEvent.isWorthInspecting(): Boolean {
        if (project == null) return false
        val commitId = getSelectedCommitId() ?: return false
        if (cherryPickSource() == null) return false
        return RemergeStatusCache.get(commitId)?.outcome != RemergeOutcome.CLEAN
    }

    private fun AnActionEvent.cherryPickSource(): String? {
        val metadata = getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION)?.cachedMetadata?.singleOrNull() ?: return null
        return parseCherryPickSource(metadata.fullMessage)
    }
}

internal fun performRemergeDiff(project: Project, commitId: CommitId, originalHash: String) {
    object : Task.Backgroundable(project, MessageBundle.message("notes.remergeDiff.progress")) {
        override fun run(indicator: ProgressIndicator) {
            val outcome = runCatching { computeRemergeDiff(project, commitId.root, commitId.hash.asString(), originalHash) }
            ApplicationManager.getApplication().invokeLater {
                outcome.fold(
                    onSuccess = { changes -> showRemergeDiff(project, changes) },
                    onFailure = { ex ->
                        Messages.showErrorDialog(project, ex.message ?: ex.toString(), MessageBundle.message("notes.remergeDiff"))
                    },
                )
            }
        }
    }.queue()
}

private fun showRemergeDiff(project: Project, changes: List<Change>) {
    if (changes.isEmpty()) {
        Messages.showInfoMessage(project, MessageBundle.message("notes.remergeDiff.noDifferences"), MessageBundle.message("notes.remergeDiff"))
        return
    }
    val producers = changes.mapNotNull { ChangeDiffRequestProducer.create(project, it) }
    DiffManager.getInstance().showDiff(project, ChangeDiffRequestChain(producers, 0), DiffDialogHints.DEFAULT)
}
