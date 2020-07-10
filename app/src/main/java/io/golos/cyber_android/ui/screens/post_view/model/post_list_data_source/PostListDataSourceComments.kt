package io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source

import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.CommentListItemState
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserBriefDomain

interface PostListDataSourceComments {
    /**
     * Adds first-level Loading indicator to the end
     */
    suspend fun addLoadingCommentsIndicator()

    /**
     * Adds first-level Retry button to the end
     */
    suspend fun addRetryLoadingComments()

    suspend fun addEmptyCommentsStub()

    suspend fun removeEmptyCommentsStub()

    suspend fun addFirstLevelComments(comments: List<CommentDomain>)

    /**
     * Adds second-level Loading indicator
     */
    suspend fun addLoadingCommentsIndicator(parentCommentId: ContentIdDomain, commentsAdded: Int)

    /**
     * Adds second-level Retry button
     */
    suspend fun addRetryLoadingComments(parentCommentId: ContentIdDomain, commentsAdded: Int)

    /**
     * [repliedAuthors] - id of all loaded comments and their authors
     */
    suspend fun addSecondLevelComments(
        parentCommentId: ContentIdDomain,
        comments: List<CommentDomain>,
        repliedAuthors: Map<ContentIdDomain, UserBriefDomain>,
        commentsAdded: Int,
        totalComments: Int,
        isEndOfDataReached: Boolean,
        nextTopCommentAuthor: UserBriefDomain?
    )

    /**
     * Adds first-level Loading indicator for a new comment
     */
    suspend fun addLoadingForNewComment()

    /**
     * Remove first-level Loading indicator for a new comment
     */
    suspend fun removeLoadingForNewComment()

    suspend fun addNewComment(comment: CommentDomain)

    suspend fun updateCommentState(commentId: ContentIdDomain, state: CommentListItemState)

    suspend fun deleteComment(commentId: ContentIdDomain)

    suspend fun deleteCommentsHeader()

    suspend fun updateComment(newComment: CommentDomain)

    suspend fun addLoadingForRepliedComment(repliedCommentId: ContentIdDomain)

    suspend fun addReplyComment(
        repliedCommentId: ContentIdDomain,
        repliedCommentAuthor: UserBriefDomain,
        repliedCommentLevel: Int,
        commentModel: CommentDomain)

    suspend fun removeLoadingForRepliedComment(repliedCommentId: ContentIdDomain)

    suspend fun updateCommentVoteStatus(
        commentId: ContentIdDomain,
        isUpVoteActive: Boolean?,
        isDownVoteActive: Boolean?,
        voteBalanceDelta: Long)

    fun isNotComments(): Boolean
}