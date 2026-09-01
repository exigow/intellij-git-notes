package notes.remerge

import com.intellij.openapi.project.Project
import com.intellij.vcs.log.impl.VcsProjectLog
import com.intellij.vcs.log.ui.frame.VcsCommitExternalStatusPresentation
import com.intellij.vcs.log.visible.filters.VcsLogFilterObject
import notes.Icons
import notes.remerge.RemergeOutcome.*
import java.awt.event.InputEvent
import javax.swing.Icon

@Suppress("UnstableApiUsage")
internal class RemergeStatusPresentation(
    private val project: Project,
    private val status: RemergeStatus,
) : VcsCommitExternalStatusPresentation.Clickable {
    private val outcome = status.outcome

    override val icon: Icon
        get() = when (outcome) {
            SCHEDULED -> Icons.SCHEDULED
            UPDATING -> Icons.UPDATING
            CLEAN -> Icons.CLEAN
            DIRTY -> Icons.DIRTY
            UNKNOWN -> Icons.UNKNOWN
            NOT_CHERRY_PICK -> error("no presentation") // todo: really error?
        }

    override val text: String get() = status.text

    override fun clickEnabled(e: InputEvent?): Boolean {
        val clickable = outcome == CLEAN
            || outcome == DIRTY
            || outcome == UNKNOWN
        val hashHashes = status.commitId != null && status.originalHash != null
        return clickable && hashHashes
    }

    override fun onClick(e: InputEvent?): Boolean {
        val commitId = status.commitId ?: return false
        val originalHash = status.originalHash ?: return false
        when (outcome) {
            CLEAN -> {
                val hashFilter = VcsLogFilterObject.fromHash(originalHash)
                val rootFilter = VcsLogFilterObject.fromRoot(commitId.root)
                val filters = VcsLogFilterObject.collection(hashFilter, rootFilter)
                VcsProjectLog.getInstance(project).openLogTab(filters)
            }

            DIRTY,
            UNKNOWN -> performRemergeDiff(project, commitId, originalHash)

            else -> return false
        }
        return true
    }
}
