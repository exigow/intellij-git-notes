package notes.remerge

import com.intellij.util.concurrency.AppExecutorUtil

internal val remergeStatusExecutor = AppExecutorUtil.createBoundedApplicationPoolExecutor("GitNotes.RemergeStatus", 1)
