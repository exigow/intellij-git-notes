package notes.remerge

import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.data.VcsCommitExternalStatus
import notes.MessageBundle
import notes.remerge.RemergeOutcome.*

enum class RemergeOutcome {
    SCHEDULED,
    UPDATING,
    NOT_CHERRY_PICK,
    CLEAN,
    DIRTY,
    UNKNOWN,
}

data class RemergeStatus(
    val outcome: RemergeOutcome = SCHEDULED,
    val originalHash: String? = null,
    val commitId: CommitId? = null,
) : VcsCommitExternalStatus {
    override fun toString() = when (outcome) {
        SCHEDULED,
        UPDATING,
        NOT_CHERRY_PICK -> ""
        CLEAN -> MessageBundle.message("notes.remergeStatus.clean", shortHash(originalHash!!))
        DIRTY -> MessageBundle.message("notes.remergeStatus.dirty", shortHash(commitId!!.hash.asString()), shortHash(originalHash!!))
        UNKNOWN -> MessageBundle.message("notes.remergeStatus.unknown", shortHash(originalHash!!))
    }

    companion object {
        val SCHEDULED_STATUS = RemergeStatus(SCHEDULED)
        val NOT_CHERRY_PICK_STATUS = RemergeStatus(NOT_CHERRY_PICK)

        private fun shortHash(hash: String) = hash.take(8)
    }
}


