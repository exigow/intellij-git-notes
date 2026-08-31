package notes.remerge

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vfs.VirtualFile
import git4idea.GitContentRevision
import git4idea.GitRevisionNumber
import git4idea.config.GitExecutableManager
import notes.MessageBundle
import java.nio.charset.StandardCharsets

class RemergeDiffException(message: String) : Exception(message)

fun computeRemergeDiff(project: Project, root: VirtualFile, commitHash: String, originalHash: String): List<Change> {
    ensureCommitExists(project, root, originalHash)
    val autoMergeTree = computeAutoMergeTree(project, root, commitHash, originalHash)
    return buildChanges(project, root, autoMergeTree, commitHash)
}

internal fun computeRemergeOutcomeGivenTree(
    project: Project,
    root: VirtualFile,
    commitHash: String,
    commitTree: String,
    originalHash: String,
    timeoutMs: Int = BULK_REMERGE_TIMEOUT_MS,
): RemergeOutcome {
    val autoMergeTree = computeAutoMergeTree(project, root, commitHash, originalHash, timeoutMs)
    return if (autoMergeTree == commitTree) RemergeOutcome.CLEAN else RemergeOutcome.DIRTY
}

internal const val BULK_REMERGE_TIMEOUT_MS = 5_000

internal fun ensureCommitExists(project: Project, root: VirtualFile, hash: String) {
    val output = runGit(project, root, "cat-file", "-e", "$hash^{commit}")
    if (output.exitCode != 0) {
        throw RemergeDiffException(MessageBundle.message("notes.remergeDiff.originalMissing", hash))
    }
}

private fun computeAutoMergeTree(project: Project, root: VirtualFile, commitHash: String, originalHash: String, timeoutMs: Int = 30_000): String {
    val output = runGit(
        project, root,
        "merge-tree", "--write-tree", "--merge-base=$originalHash^", "$commitHash^", originalHash,
        timeoutMs = timeoutMs,
    )
    if (output.exitCode != 0 && output.exitCode != 1) {
        throw RemergeDiffException(output.stderr.ifBlank { output.stdout }.ifBlank { "git merge-tree failed (exit ${output.exitCode})" })
    }
    val treeHash = output.stdoutLines.firstOrNull()?.trim()
    if (treeHash.isNullOrEmpty()) {
        throw RemergeDiffException("git merge-tree produced no output")
    }
    return treeHash
}

private fun buildChanges(project: Project, root: VirtualFile, beforeTree: String, afterCommit: String): List<Change> {
    val output = runGit(project, root, "diff", "--name-status", beforeTree, afterCommit)
    if (output.exitCode != 0) {
        throw RemergeDiffException(output.stderr.ifBlank { "git diff failed (exit ${output.exitCode})" })
    }
    val before = GitRevisionNumber(beforeTree)
    val after = GitRevisionNumber(afterCommit)
    return output.stdoutLines.map { line -> parseChange(project, root, line, before, after) }
}

private fun parseChange(project: Project, root: VirtualFile, line: String, before: GitRevisionNumber, after: GitRevisionNumber): Change {
    val tokens = line.split("\t")
    val path = { escaped: String -> GitContentRevision.createPathFromEscaped(root, escaped) }
    return when (tokens[0][0]) {
        'A' -> Change(null, revision(project, path(tokens[1]), after), FileStatus.ADDED)
        'D' -> Change(revision(project, path(tokens[1]), before), null, FileStatus.DELETED)
        'R', 'C' -> Change(
            revision(project, path(tokens[1]), before),
            revision(project, path(tokens[2]), after),
            FileStatus.MODIFIED,
        )
        else -> Change(
            revision(project, path(tokens[1]), before),
            revision(project, path(tokens[1]), after),
            FileStatus.MODIFIED,
        )
    }
}

private fun revision(project: Project, path: FilePath, revisionNumber: GitRevisionNumber) =
    GitContentRevision.createRevision(path, revisionNumber, project)

internal fun runGit(project: Project, root: VirtualFile, vararg args: String, timeoutMs: Int = 30_000): ProcessOutput {
    val git = GitExecutableManager.getInstance().getPathToGit(project)
    val command = GeneralCommandLine(git, *args).withWorkDirectory(root.path).withCharset(StandardCharsets.UTF_8)
    return ExecUtil.execAndGetOutput(command, timeoutMs)
}
