package notes

import com.intellij.openapi.components.service
import com.intellij.ui.ColoredTableCellRenderer
import com.intellij.util.ui.JBUI
import com.intellij.vcs.log.ui.table.GraphTableModel
import com.intellij.vcs.log.ui.table.VcsLogCellRenderer
import com.intellij.vcs.log.ui.table.VcsLogGraphTable
import com.intellij.vcs.log.ui.table.column.VcsLogCustomColumn
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

@Suppress("UnstableApiUsage")
internal class NotesColumn : VcsLogCustomColumn<List<Note>> {
    override val id = "GitNotes.Column"
    override val localizedName = MessageBundle.message("notes.column")
    override val isDynamic = true

    override fun getStubValue(model: GraphTableModel) = emptyList<Note>()

    override fun getValue(model: GraphTableModel, row: Int): List<Note> {
        val index = model.getId(row) ?: return emptyList()
        val commitId = model.logData.getCommitId(index) ?: return emptyList()
        return model.getNotesService().getStatus(commitId)
    }

    override fun createTableCellRenderer(table: VcsLogGraphTable): TableCellRenderer {
        table.model.getNotesService().setRepaintCallback { table.repaint() }
        return NotesCellRenderer()
    }

    // todo: replace "x notes" with simple icon
    private class NotesCellRenderer : ColoredTableCellRenderer(), VcsLogCellRenderer {
        override fun customizeCellRenderer(
            table: JTable, value: Any?, selected: Boolean, hasFocus: Boolean, row: Int, column: Int
        ) {
            @Suppress("UNCHECKED_CAST")
            val notes = value as? List<Note> ?: return
            if (notes.isEmpty() || table !is VcsLogGraphTable) return
            append(
                notes.countLabel(),
                table.applyHighlighters(this, row, column, hasFocus, selected)
            )
        }

        override fun getPreferredWidth() =
            VcsLogCellRenderer.PreferredWidth.FromData { table, value, _, _ ->
                @Suppress("UNCHECKED_CAST")
                val notes = value as? List<Note>
                if (notes.isNullOrEmpty()) null
                else table.getFontMetrics(table.font).stringWidth(notes.countLabel()) + JBUI.scale(
                    16
                )
            }

        private fun List<Note>.countLabel(): String = when (size) {
            0 -> ""
            1 -> MessageBundle.message("notes.single")
            else -> MessageBundle.message("notes.many", size)
        }
    }

    private fun GraphTableModel.getNotesService(): NotesService =
        logData.project.service<NotesService>()
}
