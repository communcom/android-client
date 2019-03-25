package io.golos.cyber_android

import io.golos.cyber_android.locator.RepositoriesHolder
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
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
                scope.launch {

                    locator.postFeedRepository.requestDiscussionUpdate(it?.discussionIdEntity ?: return@launch)
                }
            }
        }
    }
}