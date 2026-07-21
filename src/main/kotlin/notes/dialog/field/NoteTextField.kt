package notes.dialog.field

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.colors.impl.DelegateColorScheme
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import java.awt.Dimension
import java.awt.Font

class NoteTextField(
    text: String, project: Project
) : EditorTextField(text, project, PlainTextFileType.INSTANCE) {
    init {
        isOneLineMode = false
        minimumSize = calculateTerminalSize()
    }

    override fun createEditor() = super.createEditor().apply {
        setVerticalScrollbarVisible(true)
        settings.isLineNumbersShown = true
        settings.isUseSoftWraps = true
        colorsScheme = object : DelegateColorScheme(colorsScheme) {
            override fun getEditorFontName() = Font.MONOSPACED
            override fun getFont(key: EditorFontType?) = editorFont()
        }
    }

    private fun calculateTerminalSize(): Dimension {
        val metrics = getFontMetrics(editorFont())
        val w = metrics.widths.max()
        val h = metrics.height
        return Dimension(w * 80, h * 20)
    }

    private fun editorFont() = Font(
        Font.MONOSPACED, Font.PLAIN, EditorColorsManager.getInstance().globalScheme.editorFontSize
    )
}
