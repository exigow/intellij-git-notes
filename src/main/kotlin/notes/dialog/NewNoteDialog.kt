package notes.dialog

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import notes.MessageBundle
import notes.dialog.field.NoteTextField
import notes.dialog.field.NoteTopicField

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
        title = MessageBundle.message("notes.newNote")
        setOKButtonText(MessageBundle.message("notes.save"))
        setCancelButtonText(MessageBundle.message("notes.cancel"))
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

    override fun getPreferredFocusedComponent() = topicField

    override fun createCenterPanel() = panel {
        row {
            cell(topicField).align(AlignX.FILL)
        }
        row {
            cell(textArea).align(AlignX.FILL).align(AlignY.FILL)
        }.resizableRow()
    }

    override fun doValidateAll() = buildList {
        if (topic.isEmpty()) add(
            ValidationInfo(
                MessageBundle.message("notes.topicMustNotBeEmpty"), topicField
            )
        )
    }
}
