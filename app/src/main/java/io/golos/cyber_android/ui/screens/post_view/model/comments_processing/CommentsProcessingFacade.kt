package io.golos.cyber_android.ui.screens.post_view.model.comments_processing

import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.AttachmentsBlock
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.Block

interface CommentsProcessingFacade {
    val pageSize: Int

    suspend fun loadStartFirstLevelPage()

    suspend fun loadNextFirstLevelPageByScroll()

    suspend fun retryLoadFirstLevelPage()

    suspend fun loadNextSecondLevelPage(parentCommentId: DiscussionIdModel)

    suspend fun retryLoadSecondLevelPage(parentCommentId: DiscussionIdModel)

    suspend fun sendComment(jsonBody: String)

    suspend fun deleteComment(commentId: DiscussionIdModel, isSingleComment: Boolean)

    fun getCommentText(commentId: DiscussionIdModel): List<CharSequence>

    fun getComment(commentId: ContentId): CommentModel?

    fun getComment(discussionIdModel: DiscussionIdModel): CommentModel?

    suspend fun updateComment(commentId: DiscussionIdModel, jsonBody: String)

    suspend fun replyToComment(repliedCommentId: DiscussionIdModel, jsonBody: String)

    suspend fun vote(communityId: CommunityIdDomain, commentId: DiscussionIdModel, isUpVote: Boolean)
}