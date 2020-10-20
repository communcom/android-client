package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage

import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain

interface CommentsStorage {
    fun addPostedComment(comment: CommentDomain)

    fun addComment(comment: CommentDomain)

    fun isCommentPosted(id: ContentIdDomain): Boolean

    fun getComment(id: ContentIdDomain): CommentDomain?

    fun updateComment(newComment: CommentDomain)
}