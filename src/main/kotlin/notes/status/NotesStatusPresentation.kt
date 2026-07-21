package notes.status

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import com.intellij.vcs.log.ui.frame.VcsCommitExternalStatusPresentation
import notes.MessageBundle
import notes.NotesIcons
import notes.action.editNote
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
        status.topics.singleOrNull()?.let { topic ->
            editNote(project, commitId, topic)
            return true
        }
        val popup = JBPopupFactory.getInstance()
            .createPopupChooserBuilder(status.topics)
            .setItemChosenCallback { topic -> editNote(project, commitId, topic) }
            .createPopup()
        if (e is MouseEvent) popup.show(RelativePoint(e)) else popup.showInFocusCenter()
        return true
    }
}
