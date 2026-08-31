package notes.remerge

private val CHERRY_PICK_TRAILER = Regex("""\(cherry picked from commit ([0-9a-f]{7,40})\)""")

fun parseCherryPickSource(fullMessage: String): String? =
    CHERRY_PICK_TRAILER.findAll(fullMessage).lastOrNull()?.groupValues?.get(1)
