package notes.dialog

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentValidator
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
    knownTopics: Set<String>,
) : DialogWrapper(project, true, IdeModalityType.MODELESS) {
    private val topicField = NoteTopicField(project, knownTopics, initialTopic)
    private val textArea = NoteTextField("", project)
    private var topicsOnCommit: Set<String> = emptySet()
    private val topicValidator = createTopicValidator(knownTopics).installOn(topicField)
    val topic: String get() = topicField.text
    val text: String get() = textArea.text
    var onOk: (() -> Boolean)? = null

    fun setTopicsOnCommit(topics: Set<String>) {
        topicsOnCommit = topics
        topicValidator.revalidate()
    }

    override fun doOKAction() {
        if (onOk?.invoke() != false) super.doOKAction()
    }

    init {
        title = MessageBundle.message("notes.newNote")
        setOKButtonText(MessageBundle.message("notes.save"))
        setCancelButtonText(MessageBundle.message("notes.cancel"))
        init()
        val listener = object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                updateSaveEnabled()
                topicValidator.revalidate()
            }
        }
        topicField.addDocumentListener(listener)
        textArea.addDocumentListener(listener)
        updateSaveEnabled()
        topicValidator.revalidate()
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

    override fun doValidateAll() =
        if (topic.isEmpty()) listOf(ValidationInfo(MessageBundle.message("notes.topicMustNotBeEmpty"), topicField))
        else emptyList()

    private fun updateSaveEnabled() {
        isOKActionEnabled = topic.isNotEmpty() || text.isNotEmpty()
    }

    private fun createTopicValidator(
        knownTopics: Set<String>,
    ) = ComponentValidator(disposable).withValidator {
        when {
            topic.isEmpty() -> null
            topic in topicsOnCommit -> ValidationInfo(MessageBundle.message("notes.topicExistsOnCommit"), topicField)
            topic !in knownTopics -> ValidationInfo(MessageBundle.message("notes.topicIsNew"), topicField).asWarning()
            else -> null
        }
    }
}
