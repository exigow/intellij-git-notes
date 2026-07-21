package notes.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.vcs.log.CommitId

class EditNoteAction(
    private val commitId: CommitId,
    private val topic: String,
) : AnAction() {
    init {
        templatePresentation.text = topic
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        editNote(project, commitId, topic)
    }
}
