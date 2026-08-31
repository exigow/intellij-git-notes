package notes.remerge

import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.data.VcsCommitExternalStatus
import notes.MessageBundle

internal enum class RemergeOutcome {
    SCHEDULED,
    UPDATING,
    NOT_CHERRY_PICK,
    CLEAN,
    DIRTY,
    UNKNOWN,
}

internal data class RemergeStatus(
    val outcome: RemergeOutcome = RemergeOutcome.SCHEDULED,
    val originalHash: String? = null,
    val commitId: CommitId? = null,
) : VcsCommitExternalStatus {
    companion object {
        val SCHEDULED = RemergeStatus(RemergeOutcome.SCHEDULED)
        val NOT_CHERRY_PICK = RemergeStatus(RemergeOutcome.NOT_CHERRY_PICK)
    }

    override fun toString() = when (outcome) {
        RemergeOutcome.SCHEDULED -> MessageBundle.message("notes.remergeStatus.scheduled")
        RemergeOutcome.UPDATING -> MessageBundle.message("notes.remergeStatus.updating")
        RemergeOutcome.NOT_CHERRY_PICK -> ""
        RemergeOutcome.CLEAN -> MessageBundle.message("notes.remergeStatus.clean", originalHash.orEmpty())
        RemergeOutcome.DIRTY -> MessageBundle.message("notes.remergeStatus.dirty", originalHash.orEmpty())
        RemergeOutcome.UNKNOWN -> MessageBundle.message("notes.remergeStatus.unknown", originalHash.orEmpty())
    }
}
