package notes.remerge

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.vcs.log.ui.frame.VcsCommitExternalStatusPresentation
import com.intellij.vcs.log.ui.frame.VcsCommitExternalStatusProvider
import notes.MessageBundle

@Suppress("UnstableApiUsage")
internal class RemergeStatusProvider : VcsCommitExternalStatusProvider.WithColumn<RemergeStatus>() {
    override val id = "GitNotes.RemergeStatusColumn"
    override val columnName = MessageBundle.message("notes.remergeStatus.column")
    override val isColumnEnabledByDefault = true

    override fun getExternalStatusColumnService() = service<RemergeStatusColumnService>()

    override fun getStubStatus() = RemergeStatus.SCHEDULED_STATUS

    override fun getPresentation(project: Project, status: RemergeStatus): VcsCommitExternalStatusPresentation? {
        if (status.outcome == RemergeOutcome.NOT_CHERRY_PICK) return null
        return RemergeStatusPresentation(project, status)
    }
}
