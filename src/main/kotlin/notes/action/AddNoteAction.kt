package notes.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import notes.MessageBundle
import notes.NotesService
import notes.dialog.NewNoteDialog

class AddNoteAction : AnAction() {
    init {
        templatePresentation.text = MessageBundle.message("notes.newNote")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val commitId = e.getSelectedCommitId() ?: return
        val service = project.service<NotesService>()
        val dialog = NewNoteDialog(project, "", service.getAllTopics(commitId.root))
        dialog.show()
        Disposer.register(dialog.disposable) {
            if (dialog.isOK) service.addNote(commitId, dialog.topic, dialog.text)
        }
    }
}
