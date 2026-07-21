package notes.status

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.vcs.log.ui.frame.VcsCommitExternalStatusPresentation
import com.intellij.vcs.log.ui.frame.VcsCommitExternalStatusProvider
import notes.MessageBundle

@Suppress("UnstableApiUsage")
internal class NotesStatusProvider : VcsCommitExternalStatusProvider.WithColumn<NotesStatus>() {
    override val id = "GitNotes.Column"
    override val columnName = MessageBundle.message("notes.column")
    override val isColumnEnabledByDefault = true

    override fun getExternalStatusColumnService() = service<NotesStatusColumnService>()

    override fun getStubStatus() = NotesStatus.NONE

    override fun getPresentation(project: Project, status: NotesStatus): VcsCommitExternalStatusPresentation? {
        if (status.topics.isEmpty()) return null
        return NotesStatusPresentation(status)
    }
}
