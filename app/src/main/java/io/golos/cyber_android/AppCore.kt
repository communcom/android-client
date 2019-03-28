package io.golos.cyber_android

import io.golos.cyber_android.locator.RepositoriesHolder
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.VoteRequestEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
class AppCore(private val locator: RepositoriesHolder, private val dispatchersProvider: DispatchersProvider) {
    private val isInited = AtomicBoolean(false)
    private val scope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
    fun initialize() {
        if (isInited.get()) return
        synchronized(this) {
            isInited.set(true)
            locator
                .authRepository
                .makeAction(
                    locator
                        .authRepository
                        .allDataRequest
                )//todo stub for testing

            locator.voteRepository.getAsLiveData(locator.voteRepository.allDataRequest).observeForever {
                val vote = it ?: return@observeForever
                scope.launch {
                    when (vote) {
                        is VoteRequestEntity.VoteForAPostRequestEntity -> locator.postFeedRepository.requestDiscussionUpdate(
                            vote.discussionIdEntity
                        )
                        is VoteRequestEntity.VoteForACommentRequestEntity -> locator.commentsRepository.requestDiscussionUpdate(
                            vote.discussionIdEntity
                        )
                    }
                }
            }
        }
    }
}