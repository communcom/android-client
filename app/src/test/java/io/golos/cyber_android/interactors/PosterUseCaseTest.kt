package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.*
import io.golos.domain.FromSpannedToHtmlTransformer
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.interactors.feed.PostWithCommentUseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.requestmodel.QueryResult
import junit.framework.Assert.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
class PosterUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    private lateinit var useCase: DiscussionPosterUseCase


    @Before
    fun before() {
        useCase = DiscussionPosterUseCase(discussionCreationRepo, dispatchersProvider, object :
            FromSpannedToHtmlTransformer {
            override fun transform(spanned: CharSequence): String {
                return spanned.toString()
            }
        })
        appCore.initialize()
    }

    @Test
    fun postTest() = runBlocking {
        useCase.subscribe()
        useCase.unsubscribe()
        useCase.subscribe()

        var disscussionResult: QueryResult<DiscussionCreationResultModel>? = null

        useCase.getAsLiveData.observeForever {
            disscussionResult = it
        }

        useCase.createPostOrComment(PostCreationRequestModel("test title", "супер тело", listOf("nsfw")))

        assertTrue(disscussionResult is QueryResult.Loading)

        while (disscussionResult is QueryResult.Loading) delay(200)

        assertTrue(disscussionResult is QueryResult.Success)

        assertNotNull(useCase.getAsLiveData.value)

        useCase.createPostOrComment(PostCreationRequestModel("test title", "хава нагила", listOf("nsfw")))

        while (disscussionResult is QueryResult.Loading) delay(200)

        assertTrue(disscussionResult is QueryResult.Success)

        assertNotNull(useCase.getAsLiveData.value)

        val discussionCreateResult = (disscussionResult as QueryResult.Success).originalQuery as PostCreationResultModel

        useCase.createPostOrComment(
            CommentCreationRequestModel(
                "test title",
                discussionCreateResult.postId,
                listOf("nsfw")
            )
        )

        while (disscussionResult is QueryResult.Loading) delay(200)

        assertTrue(disscussionResult is QueryResult.Success)

        assertNotNull(useCase.getAsLiveData.value)

        assertTrue((disscussionResult as QueryResult.Success).originalQuery is CommentCreationResultModel)
    }

    @Test
    fun testCommentCreation() = runBlocking {

        val communityUseCase = CommunityFeedUseCase(
            CommunityId("gls"), feedRepository, voteRepo, feedEntityToModelMapper,
            dispatchersProvider
        )
        communityUseCase.subscribe()

        var posts = communityUseCase.getAsLiveData.value

        communityUseCase.getAsLiveData.observeForever {
            posts = it
        }
        communityUseCase.requestFeedUpdate(20, UpdateOption.FETCH_NEXT_PAGE)

        val post = posts?.items?.findLast { it.comments.count > 1 }!!


        val postWithCommentsUseCase = PostWithCommentUseCase(
            post.contentId, feedRepository,
            postEntityToModelMapper,
            commentsFeedRepository,
            voteRepo,
            commentFeeEntityToModelMapper,
            dispatchersProvider
        )

        postWithCommentsUseCase.subscribe()

        var comments = postWithCommentsUseCase.getAsLiveData.value

        postWithCommentsUseCase.getAsLiveData.observeForever {
            comments = it
        }
        postWithCommentsUseCase.requestFeedUpdate(1, UpdateOption.FETCH_NEXT_PAGE)

        val posterUseCase =
            DiscussionPosterUseCase(discussionCreationRepo, dispatchersProvider, object : FromSpannedToHtmlTransformer {
                override fun transform(spanned: CharSequence): String {
                    return spanned.toString()
                }
            })

        posterUseCase.subscribe()

        assertEquals(1, comments?.items?.size)

        var postingState = posterUseCase.getAsLiveData.value

        posterUseCase.getAsLiveData.observeForever {
            postingState = it
        }

        posterUseCase.createPostOrComment( //creating root comment
            CommentCreationRequestModel(
                "body", post.contentId,
                emptyList()
            )
        )

        assertTrue(postingState is QueryResult.Loading)

        delay(5_000)

        assertTrue(postingState is QueryResult.Success)

        assertEquals("root comment was not sent to post comments", 2, comments?.items?.size)

        val rootCommentCreationId = ((postingState as QueryResult.Success).originalQuery as CommentCreationResultModel)


        posterUseCase.createPostOrComment(
            CommentCreationRequestModel(
                "body", comments!!.items.first().contentId,
                emptyList()
            )
        )

        assertTrue(postingState is QueryResult.Loading)

        delay(5_000)

        assertTrue(postingState is QueryResult.Success)

        val commentCreationId = ((postingState as QueryResult.Success).originalQuery as CommentCreationResultModel)

        assertEquals("second level comment was not sent to comments list", 3, comments?.items?.size)

        postWithCommentsUseCase.requestFeedUpdate(100, UpdateOption.FETCH_NEXT_PAGE)


        assertTrue(comments!!.items.size > 3)

        assertTrue(
            "new root comment was erased",
            comments!!.items.any { it.contentId == rootCommentCreationId.commentId })
        assertTrue(
            "new secondLevle comment was erased",
            comments!!.items.any { it.contentId == commentCreationId.commentId })

        assertEquals("root comment duplicated", 1,
            comments!!.items
                .filter {
                    it.contentId == rootCommentCreationId.commentId
                }
                .map { 1 }
                .sum())


        assertEquals("second level comment duplicated", 1,
            comments!!.items
                .filter {
                    it.contentId == commentCreationId.commentId
                }
                .map { 1 }
                .sum())

    }
}