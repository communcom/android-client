package io.golos.cyber_android.ui.screens.post_view.model.comments_processing

import io.golos.domain.use_cases.model.DiscussionIdModel

interface CommentsProcessingFacade {
    val pageSize: Int

    suspend fun loadStartFirstLevelPage()

    suspend fun loadNextFirstLevelPageByScroll()

    suspend fun retryLoadFirstLevelPage()

    suspend fun loadNextSecondLevelPage(parentCommentId: DiscussionIdModel)

    suspend fun retryLoadSecondLevelPage(parentCommentId: DiscussionIdModel)

    suspend fun sendComment(commentText: String, postHasComments: Boolean)

    suspend fun deleteComment(commentId: DiscussionIdModel, isSingleComment: Boolean)

    fun getCommentText(commentId: DiscussionIdModel): List<CharSequence>

    suspend fun updateCommentText(commentId: DiscussionIdModel, newCommentText: String)

    suspend fun replyToComment(repliedCommentId: DiscussionIdModel, newCommentText: String)

    suspend fun vote(commentId: DiscussionIdModel, isUpVote: Boolean)
}