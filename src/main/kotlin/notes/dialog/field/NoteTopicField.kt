package notes.dialog.field

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.util.TextFieldCompletionProvider
import com.intellij.util.textCompletion.TextFieldWithCompletion

class NoteTopicField(
    project: Project,
    topics: Collection<String>,
    topic: String,
) : TextFieldWithCompletion(project, TopicProvider(topics), topic, true, true, false) {
    init {
        setPlaceholder("Topic")
        setShowPlaceholderWhenFocused(true)
    }

    override fun createEditor(): EditorEx = super.createEditor().apply {
        contentComponent.focusTraversalKeysEnabled = true
    }

    private class TopicProvider(private val topics: Collection<String>) : TextFieldCompletionProvider(true) {
        override fun addCompletionVariants(text: String, offset: Int, prefix: String, result: CompletionResultSet) {
            topics.forEach {
                result.addElement(LookupElementBuilder.create(it))
            }
        }
    }
}
