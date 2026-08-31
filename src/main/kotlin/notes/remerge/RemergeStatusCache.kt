package notes.remerge

import com.intellij.vcs.log.CommitId
import java.util.concurrent.ConcurrentHashMap

internal object RemergeStatusCache {
    private val cache = ConcurrentHashMap<CommitId, RemergeStatus>()

    fun get(commitId: CommitId): RemergeStatus? = cache[commitId]

    fun put(commitId: CommitId, status: RemergeStatus) {
        if (status.outcome == RemergeOutcome.UNKNOWN) return
        cache[commitId] = status
    }

    fun clear() {
        cache.clear()
    }
}
