package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage

import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel

interface CommentsStorage {
    fun addPostedComment(comment: CommentModel)

    fun addComment(comment: CommentModel)

    fun isCommentPosted(id: DiscussionIdModel): Boolean

    fun getComment(id: DiscussionIdModel): CommentModel?

    fun updateComment(newComment: CommentModel)
}