package io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source

import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.CommentListItemState
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionAuthorModel
import io.golos.domain.interactors.model.DiscussionIdModel

interface PostListDataSourceComments {
    /**
     * Adds first-level Loading indicator to the end
     */
    suspend fun addLoadingCommentsIndicator()

    /**
     * Adds first-level Retry button to the end
     */
    suspend fun addRetryLoadingComments()

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
     * [authors] - id of all loaded comments and their authors
     */
    suspend fun addSecondLevelComments(
        parentCommentId: DiscussionIdModel,
        comments: List<CommentModel>,
        authors: Map<DiscussionIdModel, DiscussionAuthorModel>,
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

    suspend fun addCommentsHeader()

    suspend fun addNewComment(comment: CommentModel)

    suspend fun updateCommentState(commentId: DiscussionIdModel, state: CommentListItemState)

    suspend fun deleteComment(commentId: DiscussionIdModel)

    suspend fun deleteCommentsHeader()
}