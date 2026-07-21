package notes.status

import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.data.VcsCommitExternalStatus

internal data class NotesStatus(val topics: List<String>, val commitId: CommitId? = null) : VcsCommitExternalStatus {
    companion object {
        val NONE = NotesStatus(emptyList())
    }
}
