package notes

import com.intellij.icons.AllIcons
import com.intellij.ui.LayeredIcon
import com.intellij.ui.icons.TextIcon
import com.intellij.util.IconUtil
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.SwingConstants

internal object NotesIcons {
    private val NOTE: Icon = AllIcons.Actions.Edit
    private val TWO: Icon by lazy { captioned("2") }
    private val THREE: Icon by lazy { captioned("3") }
    private val FOUR: Icon by lazy { captioned("4") }
    private val MANY: Icon by lazy { captioned("4+") }

    private val component = JLabel()

    fun forCount(count: Int): Icon = when {
        count <= 1 -> NOTE
        count == 2 -> TWO
        count == 3 -> THREE
        count == 4 -> FOUR
        else -> MANY
    }

    private fun captioned(text: String): Icon =
        LayeredIcon(2).apply {
            setIcon(IconUtil.scale(NOTE, component, 0.8f), 0, 6, 6)
            setIcon(TextIcon(text, component, 11f).apply { highlight = false }, 1, SwingConstants.NORTH_WEST)
        }
}
