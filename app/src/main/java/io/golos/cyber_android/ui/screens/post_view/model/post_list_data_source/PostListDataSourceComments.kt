package io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source

import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.CommentListItemState
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionIdModel

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

    suspend fun addFirstLevelComments(comments: List<CommentModel>)

    /**
     * Adds second-level Loading indicator
     */
    suspend fun addLoadingCommentsIndicator(parentCommentId: DiscussionIdModel, commentsAdded: Int)

    /**
     * Adds second-level Retry button
     */
    suspend fun addRetryLoadingComments(parentCommentId: DiscussionIdModel, commentsAdded: Int)

    /**
     * [repliedAuthors] - id of all loaded comments and their authors
     */
    suspend fun addSecondLevelComments(
        parentCommentId: DiscussionIdModel,
        comments: List<CommentModel>,
        repliedAuthors: Map<DiscussionIdModel, DiscussionAuthorModel>,
        commentsAdded: Int,
        totalComments: Int,
        isEndOfDataReached: Boolean,
        nextTopCommentAuthor: DiscussionAuthorModel?
    )

    /**
     * Adds first-level Loading indicator for a new comment
     */
    suspend fun addLoadingForNewComment()

    /**
     * Remove first-level Loading indicator for a new comment
     */
    suspend fun removeLoadingForNewComment()

    suspend fun addNewComment(comment: CommentModel)

    suspend fun updateCommentState(commentId: DiscussionIdModel, state: CommentListItemState)

    suspend fun deleteComment(commentId: DiscussionIdModel)

    suspend fun deleteCommentsHeader()

    suspend fun updateCommentText(newComment: CommentModel)

    suspend fun addLoadingForRepliedComment(repliedCommentId: DiscussionIdModel)

    suspend fun addReplyComment(
        repliedCommentId: DiscussionIdModel,
        repliedCommentAuthor: DiscussionAuthorModel,
        repliedCommentLevel: Int,
        commentModel: CommentModel)

    suspend fun removeLoadingForRepliedComment(repliedCommentId: DiscussionIdModel)

    suspend fun updateCommentVoteStatus(
        commentId: DiscussionIdModel,
        isUpVoteActive: Boolean?,
        isDownVoteActive: Boolean?,
        voteBalanceDelta: Long)
}