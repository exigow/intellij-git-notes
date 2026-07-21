package notes.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.vcs.log.CommitId
import notes.MessageBundle

class NewNoteAction(
    private val commitId: CommitId,
) : AnAction() {
    init {
        templatePresentation.text = MessageBundle.message("notes.newNote")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        newNote(project, commitId)
    }
}
