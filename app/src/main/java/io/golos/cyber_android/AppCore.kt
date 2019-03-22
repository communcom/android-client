package io.golos.cyber_android

import io.golos.cyber_android.locator.RepositoriesHolder
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
class AppCore(private val locator: RepositoriesHolder) {
    private val isInited = AtomicBoolean(false)
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
                locator.postFeedRepository.requestDiscussionUpdate(it?.discussionIdEntity ?: return@observeForever)
            }
        }
    }
}