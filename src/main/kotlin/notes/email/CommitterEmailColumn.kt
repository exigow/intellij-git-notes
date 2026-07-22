package notes.email

import com.intellij.vcs.log.VcsCommitMetadata
import notes.MessageBundle

@Suppress("UnstableApiUsage")
internal class CommitterEmailColumn : BaseEmailColumn() {
    override val id = "GitNotes.CommitterEmail"
    override val localizedName = MessageBundle.message("notes.committerEmail")

    override fun emailOf(commit: VcsCommitMetadata) = commit.committer.email
}
