package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage

import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * It's a storage of loaded and posted comments of current user
 */
class CommentsStorageImpl
@Inject
constructor(): CommentsStorage {
    private val postedComments = ConcurrentHashMap<ContentIdDomain, CommentDomain>()

    private val allComments = ConcurrentHashMap<ContentIdDomain, CommentDomain>()

    override fun addPostedComment(comment: CommentDomain) {
        postedComments[comment.contentId] = comment
        addComment(comment)
    }

    override fun addComment(comment: CommentDomain) {
        allComments[comment.contentId] = comment
    }

    override fun isCommentPosted(id: ContentIdDomain): Boolean = postedComments.contains(id) && postedComments[id] != null

    override fun getComment(id: ContentIdDomain): CommentDomain? = allComments[id]

    override fun updateComment(newComment: CommentDomain) {
        allComments[newComment.contentId] = newComment

        if(postedComments[newComment.contentId] != null) {
            postedComments[newComment.contentId] = newComment
        }
    }
}