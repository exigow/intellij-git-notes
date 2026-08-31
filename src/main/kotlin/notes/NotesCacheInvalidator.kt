package notes

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryChangeListener

internal class NotesCacheInvalidator : ProjectActivity {
    override suspend fun execute(project: Project) {
        project.messageBus.connect(project).subscribe(
            GitRepository.GIT_REPO_CHANGE,
            GitRepositoryChangeListener { project.service<NotesService>().invalidate() },
        )
    }
}
