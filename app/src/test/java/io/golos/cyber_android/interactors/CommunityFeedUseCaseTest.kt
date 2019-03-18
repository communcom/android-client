package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.feedEntityToModelMapper
import io.golos.cyber_android.feedRepository
import io.golos.domain.interactors.CommunityFeedUseCase
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.PostFeed
import io.golos.domain.interactors.model.UpdateOption
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class CommunityFeedUseCaseTest {

    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    val case = CommunityFeedUseCase(
        CommunityId("gls"),
        feedRepository,
        feedEntityToModelMapper,
        dispatchersProvider
    )

    @Test
    fun testOne()  {
        var postFeed: PostFeed? = null
        case.subscribe()
        case.getAsLiveData.observeForever {
            postFeed = it
        }
        case.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)


        print("postFeed")
        assertTrue(postFeed != null)

    }
}