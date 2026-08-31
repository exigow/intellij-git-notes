package notes

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.LayeredIcon
import com.intellij.ui.icons.TextIcon
import com.intellij.util.IconUtil
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.SwingConstants

internal object Icons {
    private val REFRESH: Icon = AllIcons.Actions.Refresh
    val UPDATING: Icon = scaledAndCentered(REFRESH)
    val SCHEDULED: Icon = scaledAndCentered(IconLoader.getTransparentIcon(REFRESH, 0.35f))
    val CLEAN: Icon = AllIcons.General.InspectionsOK
    val DIRTY: Icon = AllIcons.General.Warning
    val UNKNOWN: Icon = AllIcons.General.ContextHelp
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

    private fun scaledAndCentered(icon: Icon): Icon {
        val scaled = IconUtil.scale(icon, null, 0.5f)
        val dx = (REFRESH.iconWidth - scaled.iconWidth) / 2
        val dy = (REFRESH.iconHeight - scaled.iconHeight) / 2
        return object : Icon {
            override fun getIconWidth() = REFRESH.iconWidth
            override fun getIconHeight() = REFRESH.iconHeight
            override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) = scaled.paintIcon(c, g, x + dx, y + dy)
        }
    }
}
