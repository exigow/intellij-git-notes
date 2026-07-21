package notes.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.VcsLogDataKeys
import notes.NotesService
import notes.dialog.EditNoteDialog

fun AnActionEvent.getSelectedCommitId(): CommitId? =
    getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION)?.commits?.singleOrNull()

fun editNote(project: Project, commitId: CommitId, topic: String) {
    val service = project.service<NotesService>()
    service.loadNotes(commitId) { notes ->
        val text = notes.firstOrNull { it.topic == topic }?.text.orEmpty()
        val dialog = EditNoteDialog(project, topic, text)
        dialog.show()
        Disposer.register(dialog.disposable) {
            if (dialog.isOK) {
                if (dialog.isDeleteRequested) service.deleteNote(commitId, topic)
                else service.addNote(commitId, topic, dialog.text, force = true)
            }
        }
    }
}
