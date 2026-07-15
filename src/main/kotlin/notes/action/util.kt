package notes.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.vcs.log.CommitId
import com.intellij.vcs.log.VcsLogDataKeys

fun AnActionEvent.getSelectedCommitId(): CommitId? =
    getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION)?.commits?.singleOrNull()
