package io.golos.cyber_android.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.commentsRepository
import io.golos.cyber_android.feedRepository
import io.golos.domain.entities.CommentEntity
import io.golos.domain.entities.DiscussionsSort
import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity
import io.golos.domain.model.CommentsOfApPostUpdateRequest
import io.golos.domain.model.CommunityFeedUpdateRequest
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-26.
 */
class CommentRepositoryTest {

    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    @Test
    fun test() {
        val postRepo = feedRepository

        val commentRepo = commentsRepository

        val postRequest = CommunityFeedUpdateRequest("gls", 1, DiscussionsSort.FROM_NEW_TO_OLD, null)

        postRepo.makeAction(postRequest)

        var post: PostEntity? = null
        postRepo.getAsLiveData(postRequest).observeForever {
            post = it?.discussions?.first()
        }

        assertTrue(post != null)

        val commentRequest = CommentsOfApPostUpdateRequest(
            post!!.contentId.userId,
            post!!.contentId.permlink, post!!.contentId.refBlockNum,
            3,
            DiscussionsSort.FROM_NEW_TO_OLD,
            null
        )

        commentRepo.makeAction(commentRequest)

        var commentFeed: FeedEntity<CommentEntity>? = null

        commentRepo.getAsLiveData(commentRequest).observeForever {
            commentFeed = it
        }

        assertTrue(commentFeed != null)

        assertTrue(commentFeed!!.discussions.first().parentPostId != null)
        assertTrue(commentFeed!!.discussions[1].parentPostId != null)
        assertTrue(commentFeed!!.discussions[1].parentCommentId != null)

        assertEquals(3, commentFeed!!.discussions.size)

        commentRepo.makeAction(commentRequest.copy(sequenceKey = commentFeed!!.nextPageId))

        assertEquals(6, commentFeed!!.discussions.size)

        commentRepo.makeAction(commentRequest.copy(sequenceKey = commentFeed!!.nextPageId, limit = 2))

        assertEquals(7, commentFeed!!.discussions.size)

        commentRepo.makeAction(commentRequest.copy(sequenceKey = commentFeed!!.nextPageId, limit = 2))

        assertEquals(8, commentFeed!!.discussions.size)

    }
}