package notes.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import com.intellij.vcs.log.CommitId
import notes.NotesService
import notes.dialog.EditNoteDialog

class EditNoteAction(
    private val commitId: CommitId,
    private val topic: String,
) : AnAction() {
    init {
        templatePresentation.text = topic
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
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
}
