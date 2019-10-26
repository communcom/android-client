package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.our_comments_collection

import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel

interface OurCommentsCollection {
    fun addCommentPosted(comment: CommentModel)

    fun addComment(comment: CommentModel)

    fun isCommentPosted(id: DiscussionIdModel): Boolean

    fun getComment(id: DiscussionIdModel): CommentModel?

    fun updateComment(newComment: CommentModel)
}