package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.feedEntityToModelMapper
import io.golos.cyber_android.feedRepository
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.PostFeed
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.model.UpdateOption
import junit.framework.Assert.assertTrue
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
    fun testOne() {
        var postFeed: PostFeed? = null
        var lastUpdatedChunk: List<PostModel>? = null
        case.subscribe()
        case.getAsLiveData.observeForever {
            postFeed = it
        }
        case.getLastFetchedChunk.observeForever {
            lastUpdatedChunk = it
        }

        case.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)

        assertTrue("fail of initial loading of posts", postFeed?.items?.size == 20)
        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)


        case.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)

        assertTrue("fail of reloading first posts", postFeed?.items?.size == 20)
        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)

        case.requestFeedUpdate(20, UpdateOption.FETCH_NEXT_PAGE)

        assertTrue("error fetching second page of posts", postFeed?.items?.size == 40)
        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)

        case.requestFeedUpdate(20, UpdateOption.FETCH_NEXT_PAGE)

        assertTrue("error fetching third page of posts", postFeed?.items?.size == 60)
        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)

        case.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)
        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)


        assertTrue(
            "error loading fresh posts, after downloading 3 consecutive pages of feed",
            postFeed?.items?.size == 20
        )

    }
}