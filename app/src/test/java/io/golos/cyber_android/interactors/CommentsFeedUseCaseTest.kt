package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.commentFeeEntityToModelMapper
import io.golos.cyber_android.commentsFeedRepository
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.voteRepo
import io.golos.domain.interactors.feed.PostCommentsFeedUseCase
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.UpdateOption
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-27.
 */
class CommentsFeedUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    private lateinit var commentsUseCase: PostCommentsFeedUseCase

    @Before
    fun before() {
        commentsUseCase = PostCommentsFeedUseCase(
            DiscussionIdModel("tst2lwbafozq", "artemisfightswithhectoragainsthelios"),
            commentsFeedRepository,
            voteRepo,
            commentFeeEntityToModelMapper,
            dispatchersProvider
        )
    }

    @Test
    fun test() {
        commentsUseCase.subscribe()
        commentsUseCase.unsubscribe()
        commentsUseCase.subscribe()

        val pageSize = 3

        var commentsFeed: DiscussionsFeed<CommentModel>? = null
        var lastUpdatedChunk: List<CommentModel>? = null

        commentsUseCase.getAsLiveData.observeForever {
            commentsFeed = it
        }

        commentsUseCase.getLastFetchedChunk.observeForever {
            lastUpdatedChunk = it
        }

        commentsUseCase.requestFeedUpdate(pageSize, UpdateOption.FETCH_NEXT_PAGE)

        assertEquals("fail of initial loading of comments", pageSize, commentsFeed?.items?.size)
        assertEquals("last updated chunk update fails", pageSize, lastUpdatedChunk?.size)
        assertEquals("fail of initial loading of posts", pageSize, commentsUseCase.getAsLiveData.value?.items?.size)

        commentsUseCase.requestFeedUpdate(pageSize, UpdateOption.FETCH_NEXT_PAGE)

        assertEquals("error fetching second page of posts", pageSize * 2, commentsFeed?.items?.size)
        assertEquals("last updated chunk update fails", pageSize, lastUpdatedChunk?.size)
        assertEquals(
            "fail of initial loading of posts", pageSize * 2,
            commentsUseCase.getAsLiveData.value?.items?.size
        )
    }

}