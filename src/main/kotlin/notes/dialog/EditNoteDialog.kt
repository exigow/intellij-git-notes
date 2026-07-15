package notes.dialog

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.MessageDialogBuilder
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import notes.dialog.field.NoteTextField
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JComponent

internal class EditNoteDialog(
    project: Project,
    topic: String,
    text: String,
) : DialogWrapper(project, true, IdeModalityType.MODELESS) {
    private val originalText = text
    private val textArea = NoteTextField(text, project)

    val text: String get() = textArea.text

    var isDeleteRequested = false
        private set

    init {
        title = "Edit $topic"
        setOKButtonText("Save")
        setCancelButtonText("Cancel")
        init()

        textArea.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) = updateSaveEnabled()
        })
        updateSaveEnabled()
    }

    private fun updateSaveEnabled() {
        isOKActionEnabled = text != originalText
    }

    override fun getPreferredFocusedComponent(): JComponent = textArea

    override fun createCenterPanel(): JComponent = panel {
        row {
            cell(textArea).align(AlignX.FILL).align(AlignY.FILL)
        }.resizableRow()
    }

    override fun createActions(): Array<Action> = arrayOf(okAction, DeleteAction(), cancelAction)

    private inner class DeleteAction : DialogWrapperAction("Delete") {
        init {
            putValue(SMALL_ICON, AllIcons.Actions.GC)
        }

        override fun doAction(e: ActionEvent) {
            val confirmed = MessageDialogBuilder.yesNo("Delete note", "Are you sure you want to delete this note?")
                .ask(contentPanel)
            if (!confirmed) return
            isDeleteRequested = true
            close(OK_EXIT_CODE)
        }
    }
}
