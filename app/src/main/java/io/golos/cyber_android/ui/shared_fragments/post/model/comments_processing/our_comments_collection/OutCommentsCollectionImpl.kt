package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.our_comments_collection

import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * It's a storage of loaded and posted comments of current user
 */
class OutCommentsCollectionImpl
@Inject
constructor(): OurCommentsCollection {
    private val postedComments = ConcurrentHashMap<DiscussionIdModel, CommentModel>()

    private val allComments = ConcurrentHashMap<DiscussionIdModel, CommentModel>()

    override fun addCommentPosted(comment: CommentModel) {
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