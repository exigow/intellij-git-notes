package notes.email

import com.intellij.vcs.log.VcsCommitMetadata
import notes.MessageBundle

@Suppress("UnstableApiUsage")
internal class AuthorEmailColumn : BaseEmailColumn() {
    override val id = "GitNotes.AuthorEmail"
    override val localizedName = MessageBundle.message("notes.authorEmail")

    override fun emailOf(commit: VcsCommitMetadata) = commit.author.email
}
