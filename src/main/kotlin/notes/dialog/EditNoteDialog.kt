package notes.dialog

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.MessageDialogBuilder
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import notes.MessageBundle
import notes.dialog.field.NoteTextField
import java.awt.event.ActionEvent

internal class EditNoteDialog(
    project: Project,
    private val topic: String,
    text: String,
) : DialogWrapper(project, true, IdeModalityType.MODELESS) {
    private val originalText = text
    private val textArea = NoteTextField(text, project)
    val text: String get() = textArea.text
    var isDeleteRequested = false
        private set

    init {
        title = MessageBundle.message("notes.editTopic", topic)
        setOKButtonText(MessageBundle.message("notes.save"))
        setCancelButtonText(MessageBundle.message("notes.cancel"))
        init()

        textArea.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) = updateSaveEnabled()
        })
        updateSaveEnabled()
    }

    private fun updateSaveEnabled() {
        isOKActionEnabled = text != originalText
    }

    override fun getPreferredFocusedComponent() = textArea

    override fun createCenterPanel() = panel {
        row {
            cell(textArea).align(AlignX.FILL).align(AlignY.FILL)
        }.resizableRow()
    }

    override fun createActions() = arrayOf(DeleteAction(topic), okAction, cancelAction)

    private inner class DeleteAction(
        private val topic: String
    ) : DialogWrapperAction(MessageBundle.message("notes.delete")) {
        override fun doAction(e: ActionEvent) {
            val confirmed = MessageDialogBuilder.yesNo(
                MessageBundle.message("notes.delete"),
                MessageBundle.message("notes.deleteConfirm", topic),
            ).ask(contentPanel)
            if (!confirmed) return
            isDeleteRequested = true
            close(OK_EXIT_CODE)
        }
    }
}
