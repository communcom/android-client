package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.feedEntityToModelMapper
import io.golos.cyber_android.feedRepository
import io.golos.cyber_android.voteRepo
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.interactors.feed.UserPostFeedUseCase
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
class UserFeedUseCaseTest {

    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    val case = UserPostFeedUseCase(
        CyberUser("tst3vtfkwvzw"),
        feedRepository,
        voteRepo,
        feedEntityToModelMapper,
        dispatchersProvider
    )
    val communityFeedUseCase = CommunityFeedUseCase(
        CommunityId("gls"),
        feedRepository,
        voteRepo,
        feedEntityToModelMapper,
        dispatchersProvider
    )

    @Test
    fun testOne() {
        val pageSize = 6
        var userPostFeed: PostFeed? = null
        var userLastChunkFeed: List<PostModel>? = null

        var communititesPostFeed: PostFeed? = null
        var communititesLastChunkPostFeed: List<PostModel>? = null

        case.subscribe()
        communityFeedUseCase.subscribe()

        case.getAsLiveData.observeForever {
            userPostFeed = it
        }
        case.getLastFetchedChunk.observeForever {
            userLastChunkFeed = it
        }
        communityFeedUseCase.getAsLiveData.observeForever {
            communititesPostFeed = it
        }
        communityFeedUseCase.getLastFetchedChunk.observeForever {
            communititesLastChunkPostFeed = it
        }

        communityFeedUseCase.requestFeedUpdate(pageSize, UpdateOption.REFRESH_FROM_BEGINNING)
        case.requestFeedUpdate(pageSize, UpdateOption.REFRESH_FROM_BEGINNING)

        assertTrue("failed to load initial loading of posts", userPostFeed?.items?.size == pageSize)
        assertTrue("failed to load initial loading of posts", communititesPostFeed?.items?.size == pageSize)
        assertTrue("failed to load initial displaying initial chunk of posts", userLastChunkFeed?.size == pageSize)
        assertTrue(
            "failed to load initial displaying initial chunk of posts",
            communititesLastChunkPostFeed?.size == pageSize
        )


        communityFeedUseCase.requestFeedUpdate(pageSize, UpdateOption.REFRESH_FROM_BEGINNING)
        case.requestFeedUpdate(pageSize, UpdateOption.REFRESH_FROM_BEGINNING)

        assertTrue("failed to load initial loading of posts", userPostFeed?.items?.size == pageSize)
        assertTrue("failed to load initial loading of posts", communititesPostFeed?.items?.size == pageSize)
        assertTrue("failed to load initial displaying initial chunk of posts", userLastChunkFeed?.size == pageSize)
        assertTrue(
            "failed to load  initial displaying initial chunk of posts",
            communititesLastChunkPostFeed?.size == pageSize
        )

        communityFeedUseCase.requestFeedUpdate(pageSize, UpdateOption.FETCH_NEXT_PAGE)
        case.requestFeedUpdate(pageSize, UpdateOption.FETCH_NEXT_PAGE)

        assertTrue("failed to load post next page of user posts", userPostFeed?.items?.size == 7)
        assertTrue(
            "failed to load  post next page of community posts",
            communititesPostFeed?.items?.size == pageSize * 2
        )
        assertTrue("failed to load next chunk of posts", userLastChunkFeed?.size == 1)
        assertTrue("failed to load  nex chunk of posts", communititesLastChunkPostFeed?.size == pageSize)


        communityFeedUseCase.requestFeedUpdate(pageSize, UpdateOption.FETCH_NEXT_PAGE)
        case.requestFeedUpdate(pageSize, UpdateOption.FETCH_NEXT_PAGE)

        assertTrue("failed to load next chunk of posts", userLastChunkFeed?.size == 0)

        communityFeedUseCase.requestFeedUpdate(pageSize, UpdateOption.FETCH_NEXT_PAGE)
        case.requestFeedUpdate(pageSize, UpdateOption.FETCH_NEXT_PAGE)

        assertTrue("failed to load next chunk of posts", userLastChunkFeed?.size == 0)
    }
}