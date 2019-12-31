package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage

import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * It's a storage of loaded and posted comments of current user
 */
class CommentsStorageImpl
@Inject
constructor(): CommentsStorage {
    private val postedComments = ConcurrentHashMap<DiscussionIdModel, CommentModel>()

    private val allComments = ConcurrentHashMap<DiscussionIdModel, CommentModel>()

    override fun addPostedComment(comment: CommentModel) {
        postedComments[comment.contentId] = comment
        addComment(comment)
    }

    override fun addComment(comment: CommentModel) {
        allComments[comment.contentId] = comment
    }

    override fun isCommentPosted(id: DiscussionIdModel): Boolean = postedComments[id] != null

    override fun getComment(id: DiscussionIdModel): CommentModel? = allComments[id]

    override fun updateComment(newComment: CommentModel) {
        allComments[newComment.contentId] = newComment

        if(postedComments[newComment.contentId] != null) {
            postedComments[newComment.contentId] = newComment
        }
    }
}