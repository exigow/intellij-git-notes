package notes.remerge

import com.intellij.openapi.project.Project
import com.intellij.vcs.log.ui.frame.VcsCommitExternalStatusPresentation
import notes.Icons
import java.awt.event.InputEvent
import javax.swing.Icon

internal class RemergeStatusPresentation(
    private val project: Project,
    private val status: RemergeStatus,
) : VcsCommitExternalStatusPresentation.Clickable {
    private val outcome = status.outcome

    override val icon: Icon
        get() = when (outcome) {
            RemergeOutcome.SCHEDULED -> Icons.SCHEDULED
            RemergeOutcome.UPDATING -> Icons.UPDATING
            RemergeOutcome.CLEAN -> Icons.CLEAN
            RemergeOutcome.DIRTY -> Icons.DIRTY
            RemergeOutcome.UNKNOWN -> Icons.UNKNOWN
            RemergeOutcome.NOT_CHERRY_PICK -> error("NOT_CHERRY_PICK has no presentation")
        }

    override val text: String
        get() = status.toString()

    override fun clickEnabled(e: InputEvent?) =
        (outcome == RemergeOutcome.DIRTY || outcome == RemergeOutcome.UNKNOWN) &&
            status.commitId != null && status.originalHash != null

    override fun onClick(e: InputEvent?): Boolean {
        if (outcome != RemergeOutcome.DIRTY && outcome != RemergeOutcome.UNKNOWN) return false
        val commitId = status.commitId ?: return false
        val originalHash = status.originalHash ?: return false
        performRemergeDiff(project, commitId, originalHash)
        return true
    }
}
