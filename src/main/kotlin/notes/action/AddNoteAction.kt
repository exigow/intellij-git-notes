package notes.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import notes.dialog.NewNoteDialog
import notes.NotesService

class AddNoteAction : AnAction() {
    init {
        templatePresentation.text = "New Note…"
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
