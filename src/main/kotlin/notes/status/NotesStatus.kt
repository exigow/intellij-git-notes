package notes.status

import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.data.VcsCommitExternalStatus
import notes.MessageBundle

internal enum class NotesPhase {
    SCHEDULED,
    UPDATING,
}

internal data class NotesStatus(
    val topics: List<String>? = null,
    val commitId: CommitId? = null,
    val phase: NotesPhase = NotesPhase.SCHEDULED,
) : VcsCommitExternalStatus {
    companion object {
        val PENDING = NotesStatus()
    }

    val isPending get() = topics == null

    override fun toString() = when (topics?.size) {
        null -> when (phase) {
            NotesPhase.SCHEDULED -> MessageBundle.message("notes.status.scheduled")
            NotesPhase.UPDATING -> MessageBundle.message("notes.status.updating")
        }
        0 -> ""
        1 -> MessageBundle.message("notes.statusSingle")
        else -> MessageBundle.message("notes.statusMultiple", topics.size)
    }
}
