package notes.remerge

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.vcs.log.data.util.VcsCommitsDataLoader
import com.intellij.vcs.log.ui.table.column.util.VcsLogExternalStatusColumnService
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
internal class RemergeStatusColumnService(override val scope: CoroutineScope) :
    VcsLogExternalStatusColumnService<RemergeStatus>() {
    override fun getDataLoader(project: Project): VcsCommitsDataLoader<RemergeStatus> = RemergeStatusLoader(project)
}
