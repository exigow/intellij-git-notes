package notes.action

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.util.containers.map2Array
import notes.MessageBundle
import notes.NotesService

class EditNoteGroup : ActionGroup() {
    init {
        templatePresentation.text = MessageBundle.message("notes.edit")
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val commitId = e?.getSelectedCommitId() ?: return emptyArray()
        return e.project?.service<NotesService>()
            ?.getStatus(commitId)
            .orEmpty()
            .map2Array { EditNoteAction(commitId, it.topic) }
    }
}
