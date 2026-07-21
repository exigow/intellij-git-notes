package notes.status

import com.intellij.vcs.log.ui.frame.VcsCommitExternalStatusPresentation
import notes.NotesIcons
import javax.swing.Icon

internal class NotesStatusPresentation(private val status: NotesStatus) : VcsCommitExternalStatusPresentation {
    override val icon: Icon
        get() = NotesIcons.forCount(status.topics.size)

    override val text: String
        get() = status.topics.joinToString("\n")
}
