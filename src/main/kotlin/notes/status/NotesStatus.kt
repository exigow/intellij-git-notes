package notes.status

import com.intellij.vcs.log.data.VcsCommitExternalStatus

internal data class NotesStatus(val topics: List<String>) : VcsCommitExternalStatus {
    companion object {
        val NONE = NotesStatus(emptyList())
    }
}
