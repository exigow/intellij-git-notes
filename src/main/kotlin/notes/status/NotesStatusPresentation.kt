package notes.status

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import com.intellij.vcs.log.ui.frame.VcsCommitExternalStatusPresentation
import notes.Icons
import notes.Icons.NOTE_4
import notes.Icons.NOTE_4_PLUS
import notes.Icons.NOTE
import notes.Icons.NOTE_3
import notes.Icons.NOTE_2
import notes.action.EditNoteAction
import notes.action.NewNoteAction
import java.awt.event.InputEvent
import java.awt.event.MouseEvent
import javax.swing.Icon

internal class NotesStatusPresentation(
    private val project: Project,
    private val status: NotesStatus,
) : VcsCommitExternalStatusPresentation.Clickable {
    override val icon: Icon
        get() = when {
            !status.isPending -> getNoteIconByCount(status.topics.orEmpty().size)
            status.phase == NotesPhase.UPDATING -> Icons.UPDATING
            else -> Icons.SCHEDULED
        }

    override val text: String
        get() = if (status.isPending) status.toString() else status.topics.orEmpty().joinToString(", ")

    override fun clickEnabled(e: InputEvent?) =
        !status.isPending && status.commitId != null && status.topics.orEmpty().isNotEmpty()

    override fun onClick(e: InputEvent?): Boolean {
        if (status.isPending) return false
        val commitId = status.commitId ?: return false
        val group = DefaultActionGroup().apply {
            status.topics.orEmpty().forEach { add(EditNoteAction(commitId, it)) }
            addSeparator()
            add(NewNoteAction(commitId))
        }
        val popup = JBPopupFactory.getInstance().createActionGroupPopup(
            null,
            group,
            SimpleDataContext.getProjectContext(project),
            false,
            null,
            -1,
        )
        if (e is MouseEvent) popup.show(RelativePoint(e)) else popup.showInFocusCenter()
        return true
    }

    private fun getNoteIconByCount(count: Int): Icon = when {
        count <= 1 -> NOTE
        count == 2 -> NOTE_2
        count == 3 -> NOTE_3
        count == 4 -> NOTE_4
        else -> NOTE_4_PLUS
    }
}
