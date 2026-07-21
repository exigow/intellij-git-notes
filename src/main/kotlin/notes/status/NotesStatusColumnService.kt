package notes.status

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.vcs.log.data.util.VcsCommitsDataLoader
import com.intellij.vcs.log.ui.table.column.util.VcsLogExternalStatusColumnService
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
internal class NotesStatusColumnService(override val scope: CoroutineScope) :
    VcsLogExternalStatusColumnService<NotesStatus>() {
    override fun getDataLoader(project: Project): VcsCommitsDataLoader<NotesStatus> = NotesStatusLoader(project)
}
