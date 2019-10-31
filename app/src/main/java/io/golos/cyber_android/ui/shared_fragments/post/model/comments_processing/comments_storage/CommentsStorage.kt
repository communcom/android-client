package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.comments_storage

import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel

interface CommentsStorage {
    fun addPostedComment(comment: CommentModel)

    fun addComment(comment: CommentModel)

    fun isCommentPosted(id: DiscussionIdModel): Boolean

    fun getComment(id: DiscussionIdModel): CommentModel?

    fun updateComment(newComment: CommentModel)
}