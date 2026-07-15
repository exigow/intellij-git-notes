package notes.dialog.field

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.colors.impl.DelegateColorScheme
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import java.awt.Dimension
import java.awt.Font
import java.awt.event.InputEvent.SHIFT_DOWN_MASK
import java.awt.event.KeyEvent.VK_TAB
import javax.swing.KeyStroke.getKeyStroke

class NoteTextField(
    text: String,
    project: Project
) : EditorTextField(text, project, PlainTextFileType.INSTANCE) {
    init {
        isOneLineMode = false
        preferredSize = calculateSize()
    }

    override fun createEditor(): EditorEx = super.createEditor().apply {
        setVerticalScrollbarVisible(true)
        settings.isUseSoftWraps = true
        backgroundColor = colorsScheme.defaultBackground
        colorsScheme = object : DelegateColorScheme(colorsScheme) {
            override fun getEditorFontName() = Font.MONOSPACED
            override fun getFont(key: EditorFontType?) = Font(Font.MONOSPACED, Font.PLAIN, editorFontSize)
        }
        focusAction { contentComponent.transferFocus() }
            .registerCustomShortcutSet(CustomShortcutSet(getKeyStroke(VK_TAB, 0)), contentComponent)
        focusAction { contentComponent.transferFocusBackward() }
            .registerCustomShortcutSet(CustomShortcutSet(getKeyStroke(VK_TAB, SHIFT_DOWN_MASK)), contentComponent)
    }

    private fun focusAction(move: () -> Unit): AnAction = object : AnAction() {
        override fun actionPerformed(e: AnActionEvent) = move()
    }

    private fun calculateSize(): Dimension {
        val metrics = getFontMetrics(Font(Font.MONOSPACED, Font.PLAIN, font.size))
        return Dimension(metrics.widths.max() * 80, metrics.height * 20)
    }
}
