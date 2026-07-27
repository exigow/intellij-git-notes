package notes.status

import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.data.VcsCommitExternalStatus
import notes.MessageBundle

internal data class NotesStatus(val topics: List<String>, val commitId: CommitId? = null) : VcsCommitExternalStatus {
    companion object {
        val NONE = NotesStatus(emptyList())
    }

    override fun toString() = when (topics.size) {
        0 -> ""
        1 -> MessageBundle.message("notes.statusSingle")
        else -> MessageBundle.message("notes.statusMultiple", topics.size)
    }
}
