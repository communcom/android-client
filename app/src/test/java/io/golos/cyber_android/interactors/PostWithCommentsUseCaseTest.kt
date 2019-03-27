package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.*
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.PostWithCommentUseCase
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.model.QueryResult
import io.golos.domain.model.VoteRequestModel
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-27.
 */
class PostWithCommentsUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    private lateinit var postWithCommmentsUseCase: PostWithCommentUseCase
    private lateinit var voteUseCase: VoteUseCase
    private val postId = DiscussionIdModel("tst2lwbafozq", "artemisfightswithhectoragainsthelios", 320697L)

    @Before
    fun before() {
        postWithCommmentsUseCase = PostWithCommentUseCase(
            postId,
            feedRepository,
            postEntityToModelMapper,
            commentsFeedRepository,
            voteRepo,
            commentFeeEntityToModelMapper,
            dispatchersProvider
        )
        voteUseCase = VoteUseCase(
            authStateRepository, voteRepo,
            dispatchersProvider, voteEntityToPostMapper,
            voteToEntityMapper
        )
        appCore.initialize()
    }

    @Test
    fun test() = runBlocking {
        postWithCommmentsUseCase.subscribe()
        postWithCommmentsUseCase.unsubscribe()
        postWithCommmentsUseCase.subscribe()
        voteUseCase.subscribe()

        val pageSize = 3

        var commentsFeed: DiscussionsFeed<CommentModel>? = null
        var lastUpdatedChunk: List<CommentModel>? = null

        postWithCommmentsUseCase.getAsLiveData.observeForever {
            commentsFeed = it
        }

        postWithCommmentsUseCase.getLastFetchedChunk.observeForever {
            lastUpdatedChunk = it
        }

        postWithCommmentsUseCase.requestFeedUpdate(pageSize, UpdateOption.FETCH_NEXT_PAGE)

        assertEquals("fail of initial loading of comments", pageSize, commentsFeed?.items?.size)
        assertEquals("last updated chunk update fails", pageSize, lastUpdatedChunk?.size)
        assertEquals(
            "fail of initial loading of posts",
            pageSize,
            postWithCommmentsUseCase.getAsLiveData.value?.items?.size
        )

        postWithCommmentsUseCase.requestFeedUpdate(pageSize, UpdateOption.FETCH_NEXT_PAGE)

        assertEquals("error fetching second page of posts", pageSize * 2, commentsFeed?.items?.size)
        assertEquals("last updated chunk update fails", pageSize, lastUpdatedChunk?.size)
        assertEquals(
            "fail of initial loading of posts", pageSize * 2,
            postWithCommmentsUseCase.getAsLiveData.value?.items?.size
        )

        postWithCommmentsUseCase.getPostAsLiveData.observeForever {

        }

        assertTrue(postWithCommmentsUseCase.getPostAsLiveData.value != null)


        voteUseCase.vote(
            VoteRequestModel.VoteForPostRequest(
                (Math.random() * 10_000).toShort(),
                postId
            )
        )

        var voteForAPost: QueryResult<VoteRequestModel>? = null
        voteUseCase.getAsLiveData.observeForever {
            voteForAPost = it?.values?.firstOrNull()
        }

        while (voteForAPost !is QueryResult.Success) delay(200)


        delay(2_000)

        assertTrue(postWithCommmentsUseCase.getPostAsLiveData.value!!.votes.hasUpVote)

    }
}