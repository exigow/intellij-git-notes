package notes.remerge

import com.intellij.codeInsight.daemon.impl.HintRenderer
import com.intellij.diff.DiffContext
import com.intellij.diff.DiffExtension
import com.intellij.diff.EditorDiffViewer
import com.intellij.diff.FrameDiffTool
import com.intellij.diff.requests.DiffRequest
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.UserDataHolder
import com.intellij.openapi.vcs.changes.ui.ChangeDiffRequestChain
import notes.MessageBundle

private val REMERGE_DIFF = Key.create<Boolean>("notes.remerge.diff")

internal class RemergeDiffProducer(
    private val delegate: ChangeDiffRequestChain.Producer,
) : ChangeDiffRequestChain.Producer by delegate {
    override fun process(context: UserDataHolder, indicator: ProgressIndicator): DiffRequest =
        delegate.process(context, indicator).also { it.putUserData(REMERGE_DIFF, true) }
}

class RemergeMarkerDiffExtension : DiffExtension() {
    override fun onViewerCreated(viewer: FrameDiffTool.DiffViewer, context: DiffContext, request: DiffRequest) {
        if (request.getUserData(REMERGE_DIFF) != true) return
        if (viewer !is EditorDiffViewer) return
        viewer.editors.forEach { addMarkerHints(it, viewer) }
    }
}

private fun addMarkerHints(editor: Editor, parent: Disposable) {
    val document = editor.document
    for (line in 0 until document.lineCount) {
        val end = document.getLineEndOffset(line)
        val label = markerLabel(document.getText(TextRange(document.getLineStartOffset(line), end))) ?: continue
        val inlay = editor.inlayModel.addAfterLineEndElement(end, false, HintRenderer(label)) ?: continue
        Disposer.register(parent, inlay)
    }
}

private fun markerLabel(line: String): String? = when {
    line.isMarker("<<<<<<<") -> MessageBundle.message("notes.remergeDiff.marker.yours")
    line.isMarker("|||||||") -> MessageBundle.message("notes.remergeDiff.marker.base")
    line.isMarker(">>>>>>>") -> MessageBundle.message("notes.remergeDiff.marker.theirs")
    else -> null
}

private fun String.isMarker(marker: String) =
    startsWith(marker) && (length == marker.length || this[marker.length] == ' ')
