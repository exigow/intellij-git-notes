package notes.email

import com.intellij.ui.ColoredTableCellRenderer
import com.intellij.vcs.log.VcsCommitMetadata
import com.intellij.vcs.log.ui.table.GraphTableModel
import com.intellij.vcs.log.ui.table.VcsLogGraphTable
import com.intellij.vcs.log.ui.table.VcsLogTableIndex
import com.intellij.vcs.log.ui.table.column.VcsLogCustomColumn
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

@Suppress("UnstableApiUsage")
internal abstract class BaseEmailColumn : VcsLogCustomColumn<String> {
    override val isDynamic = true

    override fun isEnabledByDefault() = false

    protected abstract fun emailOf(commit: VcsCommitMetadata): String

    override fun getValue(model: GraphTableModel, row: VcsLogTableIndex): String? =
        model.getCommitMetadata(row, true)?.let(::emailOf)

    override fun getStubValue(model: GraphTableModel) = ""

    override fun createTableCellRenderer(table: VcsLogGraphTable): TableCellRenderer =
        object : ColoredTableCellRenderer() {
            override fun customizeCellRenderer(
                table: JTable,
                value: Any?,
                selected: Boolean,
                hasFocus: Boolean,
                row: Int,
                column: Int,
            ) {
                append(value?.toString().orEmpty())
            }
        }
}
