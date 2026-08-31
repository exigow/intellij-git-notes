package notes.remerge

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryChangeListener

internal class RemergeCacheInvalidator : ProjectActivity {
    override suspend fun execute(project: Project) {
        project.messageBus.connect(project).subscribe(
            GitRepository.GIT_REPO_CHANGE,
            GitRepositoryChangeListener { RemergeStatusCache.clear() },
        )
    }
}
