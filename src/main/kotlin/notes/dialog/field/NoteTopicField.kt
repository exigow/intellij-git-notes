package notes.dialog.field

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.util.TextFieldCompletionProvider
import com.intellij.util.textCompletion.TextFieldWithCompletion
import notes.MessageBundle

class NoteTopicField(
    project: Project,
    topics: Collection<String>,
    topic: String,
) : TextFieldWithCompletion(project, TopicProvider(topics), topic, true, true, false) {
    init {
        setPlaceholder(MessageBundle.message("notes.topic"))
        setShowPlaceholderWhenFocused(true)
    }

    private class TopicProvider(
        private val topics: Collection<String>
    ) : TextFieldCompletionProvider(true) {
        override fun addCompletionVariants(
            text: String,
            offset: Int,
            prefix: String,
            result: CompletionResultSet
        ) {
            val lookups = topics.map { LookupElementBuilder.create(it) }
            result.addAllElements(lookups)
        }
    }
}
