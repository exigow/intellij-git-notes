package notes.status

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import com.intellij.vcs.log.ui.frame.VcsCommitExternalStatusPresentation
import notes.NotesIcons
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
        get() = NotesIcons.forCount(status.topics.size)

    override val text: String
        get() = status.topics.joinToString(", ")

    override fun clickEnabled(e: InputEvent?) = status.commitId != null && status.topics.isNotEmpty()

    override fun onClick(e: InputEvent?): Boolean {
        val commitId = status.commitId ?: return false
        val group = DefaultActionGroup().apply {
            status.topics.forEach { add(EditNoteAction(commitId, it)) }
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
}
