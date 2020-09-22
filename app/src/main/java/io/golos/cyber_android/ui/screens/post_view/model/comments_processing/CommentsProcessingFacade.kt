package io.golos.cyber_android.ui.screens.post_view.model.comments_processing

import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.ControlMetadata

interface CommentsProcessingFacade {
    val pageSize: Int

    suspend fun loadStartFirstLevelPage()

    suspend fun loadNextFirstLevelPageByScroll()

    suspend fun retryLoadFirstLevelPage()

    suspend fun loadNextSecondLevelPage(parentCommentId: ContentIdDomain)

    suspend fun retryLoadSecondLevelPage(parentCommentId: ContentIdDomain)

    suspend fun sendComment(jsonBody: String, metadata: List<ControlMetadata>)

    suspend fun deleteComment(commentId: ContentIdDomain, isSingleComment: Boolean)

    fun getCommentText(commentId: ContentIdDomain): List<CharSequence>

    fun getComment(commentId: ContentIdDomain): CommentDomain?

    suspend fun updateComment(commentId: ContentIdDomain, jsonBody: String)

    suspend fun replyToComment(repliedCommentId: ContentIdDomain, jsonBody: String, metadata: List<ControlMetadata>)

    suspend fun vote(communityId: CommunityIdDomain, commentId: ContentIdDomain, isUpVote: Boolean)
}