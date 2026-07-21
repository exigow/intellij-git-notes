package notes.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageDialogBuilder
import com.intellij.openapi.util.Disposer
import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.VcsLogDataKeys
import notes.MessageBundle
import notes.NotesService
import notes.dialog.EditNoteDialog
import notes.dialog.NewNoteDialog

fun AnActionEvent.getSelectedCommitId(): CommitId? =
    getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION)?.commits?.singleOrNull()

fun newNote(project: Project, commitId: CommitId) {
    val service = project.service<NotesService>()
    val dialog = NewNoteDialog(project, "", service.getAllTopics(commitId.root))
    dialog.show()
    Disposer.register(dialog.disposable) {
        if (!dialog.isOK) return@register
        val topic = dialog.topic
        val exists = service.matchesTopics(commitId.root, commitId.hash.asString(), setOf(topic))
        if (exists && !MessageDialogBuilder.yesNo(
                MessageBundle.message("notes.newNote"),
                MessageBundle.message("notes.topicExistsConfirm", topic),
            ).ask(project)
        ) return@register
        service.addNote(commitId, topic, dialog.text, force = exists)
    }
}

fun editNote(project: Project, commitId: CommitId, topic: String) {
    val service = project.service<NotesService>()
    service.loadNotes(commitId) { notes ->
        val text = notes.firstOrNull { it.topic == topic }?.text.orEmpty()
        val dialog = EditNoteDialog(project, topic, text)
        dialog.show()
        Disposer.register(dialog.disposable) {
            if (dialog.isOK) {
                if (dialog.isDeleteRequested) service.deleteNote(commitId, topic)
                else service.addNote(commitId, topic, dialog.text, force = true)
            }
        }
    }
}
