package notes.dialog

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import notes.dialog.field.NoteTextField
import notes.dialog.field.NoteTopicField
import javax.swing.JComponent

internal class NewNoteDialog(
    project: Project,
    initialTopic: String,
    topics: Set<String>,
) : DialogWrapper(project, true, IdeModalityType.MODELESS) {
    private val topicField = NoteTopicField(project, topics, initialTopic)
    private val textArea = NoteTextField("", project)

    val topic: String get() = topicField.text
    val text: String get() = textArea.text

    init {
        title = "New note"
        setOKButtonText("OK")
        setCancelButtonText("Cancel")
        init()

        val listener = object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) = updateSaveEnabled()
        }
        topicField.addDocumentListener(listener)
        textArea.addDocumentListener(listener)
        updateSaveEnabled()
    }

    private fun updateSaveEnabled() {
        isOKActionEnabled = topic.isNotEmpty() || text.isNotEmpty()
    }

    override fun getPreferredFocusedComponent(): JComponent = topicField

    override fun createCenterPanel(): JComponent = panel {
        row {
            cell(topicField).align(AlignX.FILL)
        }
        row {
            cell(textArea).align(AlignX.FILL).align(AlignY.FILL)
        }.resizableRow()
    }

    override fun doValidateAll(): List<ValidationInfo> = buildList {
        if (topic.isEmpty()) add(ValidationInfo("Topic must not be empty", topicField))
    }
}
