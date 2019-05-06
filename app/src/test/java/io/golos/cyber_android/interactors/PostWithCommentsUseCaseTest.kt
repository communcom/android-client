package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.*
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.interactors.feed.PostWithCommentUseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.VoteRequestModel
import junit.framework.Assert.*
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
    private lateinit var coomunityFeedUseCase: CommunityFeedUseCase

    private lateinit var voteUseCase: VoteUseCase
    private val postId = DiscussionIdModel("tst1ggrtqzvl", "zeusfightswithsemeleagainsteurynome", 140310)

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
        coomunityFeedUseCase = CommunityFeedUseCase(
            CommunityId("gls"),
            feedRepository, voteRepo, feedEntityToModelMapper, dispatchersProvider
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
        var post = postWithCommmentsUseCase.getPostAsLiveData.value

        postWithCommmentsUseCase.getPostAsLiveData.observeForever {
            post = it
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

        postWithCommmentsUseCase.unsubscribe()
        voteUseCase.unsubscribe()
    }

    @Test
    fun testMoveFromFeedToAPost() = runBlocking {
        coomunityFeedUseCase.subscribe()
        voteUseCase.subscribe()


        var feed: DiscussionsFeed<PostModel>? = null

        coomunityFeedUseCase.getAsLiveData.observeForever {
            feed = it
        }

        coomunityFeedUseCase.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)

        assertEquals("error fetching post from backend", 20, feed?.items?.size)

        val postWithComments = feed?.items?.find { it.comments.count > 0 }!!

        postWithCommmentsUseCase = PostWithCommentUseCase(
            postWithComments.contentId,
            feedRepository,
            postEntityToModelMapper,
            commentsFeedRepository,
            voteRepo,
            commentFeeEntityToModelMapper,
            dispatchersProvider
        )


        var post: PostModel? = null


        var counter = -1

        postWithCommmentsUseCase.getPostAsLiveData.observeForever {
            post = it
            counter++

            println("counter = $counter")

            when (counter) {
                0 -> {
                    assertNotNull("post was not fetched for some reason", post)
                    assertNotNull("post body-preview was null, it must not be that", post?.content?.body?.preview)
                }
                1 -> {
                    assertNotNull("post was not fetched for some reason", post)
                    assertNotNull("post body-preview was null, it must not be that", post?.content?.body?.preview)
                    assertNotNull("post body-full was null, it must not be that", post?.content?.body?.full)
                }
            }
        }
        postWithCommmentsUseCase.subscribe()

        assertNotNull("post was not fetched for some reason", post)

        while (counter < 1) delay(200)

        postWithCommmentsUseCase.unsubscribe()
        coomunityFeedUseCase.unsubscribe()
        voteUseCase.unsubscribe()
    }

    @Test
    fun testVoteForAComment() = runBlocking {
        coomunityFeedUseCase.subscribe()
        voteUseCase.subscribe()


        var feed: DiscussionsFeed<PostModel>? = null

        coomunityFeedUseCase.getAsLiveData.observeForever {
            feed = it
        }

        coomunityFeedUseCase.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)

        assertEquals("error fetching post from backend", 20, feed?.items?.size)

        val postWithComments = feed?.items?.find { it.comments.count > 0 }!!

        postWithCommmentsUseCase = PostWithCommentUseCase(
            postWithComments.contentId,
            feedRepository,
            postEntityToModelMapper,
            commentsFeedRepository,
            voteRepo,
            commentFeeEntityToModelMapper,
            dispatchersProvider
        )


        postWithCommmentsUseCase.subscribe()

        var comments: DiscussionsFeed<CommentModel>? = null

        postWithCommmentsUseCase.getAsLiveData.observeForever {
            comments = it
        }

        postWithCommmentsUseCase.requestFeedUpdate(1, UpdateOption.FETCH_NEXT_PAGE)

        var post = postWithCommmentsUseCase.getPostAsLiveData.value

        postWithCommmentsUseCase.getPostAsLiveData.observeForever {
            post = it
        }


        assertEquals("fail of initial loading of comments", 1, comments?.items?.size)
        assertTrue(post!!.content.body.preview.isNotEmpty())
        assertTrue(post!!.content.body.full.isNotEmpty())

        val commentToVote = comments!!.items.first()
        var votResult: QueryResult<VoteRequestModel>? = null

        voteUseCase.getAsLiveData.observeForever {
            votResult = it[commentToVote.contentId]
        }

        voteUseCase.vote(VoteRequestModel.VoteForComentRequest(randomPositiveVotePower(), commentToVote.contentId))

        while (votResult !is QueryResult.Success) delay(200)

        delay(2_000)

        assertTrue(comments!!.items.first().votes.hasUpVote)

        voteUseCase.vote(VoteRequestModel.VoteForComentRequest(randomNegativeVotePower(), commentToVote.contentId))

        while (votResult !is QueryResult.Success) delay(200)

        delay(2_000)

        assertTrue(comments!!.items.first().votes.hasDownVote)
    }
}

public fun randomPositiveVotePower() = (Math.random() * 10_000).toShort()
public fun randomNegativeVotePower() = (Math.random() * -10_000).toShort()