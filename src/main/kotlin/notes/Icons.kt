package notes

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.LayeredIcon
import com.intellij.ui.icons.TextIcon
import com.intellij.util.IconUtil
import com.intellij.util.ui.ColorIcon
import java.awt.Color
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.SwingConstants

object Icons {
    val UPDATING: Icon = AnimatedIcon.Default.ICONS.first()
    val SCHEDULED: Icon = IconLoader.getTransparentIcon(UPDATING, 0.3f)
    val CHERRY_PICK: Icon = loadCherryPickIcon()
    val CLEAN: Icon = CHERRY_PICK
    val DIRTY: Icon = cherryPicked(ColorIcon(8, 8, 8, 8, Color(0xF4AF3D), false, 8)) // todo: fix color
    val UNKNOWN: Icon = AllIcons.General.Error
    val NOTE: Icon = AllIcons.Actions.Edit
    val NOTE_2: Icon = captioned("2")
    val NOTE_3: Icon = captioned("3")
    val NOTE_4: Icon = captioned("4")
    val NOTE_4_PLUS: Icon = captioned("4+")

    private fun captioned(text: String): Icon =
        LayeredIcon(2).apply {
            val component = JLabel()
            setIcon(IconUtil.scale(NOTE, component, 0.8f), 0, 6, 6)
            setIcon(TextIcon(text, component, 11f).apply { highlight = false }, 1, SwingConstants.NORTH_WEST)
        }

    private fun cherryPicked(badge: Icon): Icon =
        LayeredIcon(2).apply {
            val component = JLabel()
            setIcon(IconUtil.scale(CHERRY_PICK, component, 0.8f), 0, 6, 6)
            setIcon(badge, 1, SwingConstants.NORTH_WEST)
        }

    // known issue: we could simply use DvcsImplIcons.CherryPick but those icons are marked as internal API
    private fun loadCherryPickIcon() =
        IconLoader.getIcon("/icons/cherryPick.svg", Icons::class.java)
}
