package io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source

import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.CommentListItemState
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.FirstLevelCommentListItem
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.SecondLevelCommentListItem
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.utils.IdUtil

object CommentsMapper {
    fun mapToFirstLevel(model: CommentModel, currentUserId: String): FirstLevelCommentListItem {
        if(model.content.commentLevel != 0) {
            throw UnsupportedOperationException("This level of comment is not supported: ${model.content.commentLevel}")
        }

        return FirstLevelCommentListItem(
            id = IdUtil.generateLongId(),
            version = 0,
            externalId = model.contentId,
            author = model.author,
            currentUserId = currentUserId,
            content = model.content.body.postBlock,
            voteBalance = model.votes.upCount - model.votes.downCount,
            isUpVoteActive = model.votes.hasUpVote,
            isDownVoteActive = model.votes.hasDownVote,
            metadata = model.meta,
            state = CommentListItemState.NORMAL
        )
    }

    fun mapToSecondLevel(
        model: CommentModel,
        currentUserId: String,
        parentAuthors: Map<DiscussionIdModel, DiscussionAuthorModel>): SecondLevelCommentListItem {
        if(model.content.commentLevel != 1) {
            throw UnsupportedOperationException("This level of comment is not supported: ${model.content.commentLevel}")
        }

        val repliedAuthor = model.parentId?.let { parentAuthors[it] }
        val repliedCommentLevel = if(repliedAuthor == null) 0 else 1

        return mapToSecondLevel(model, currentUserId, repliedAuthor, repliedCommentLevel)
    }

    fun mapToSecondLevel(
        model: CommentModel,
        currentUserId: String,
        repliedAuthor: DiscussionAuthorModel?,
        repliedCommentLevel: Int): SecondLevelCommentListItem {
        if(model.content.commentLevel != 1) {
            throw UnsupportedOperationException("This level of comment is not supported: ${model.content.commentLevel}")
        }

        return SecondLevelCommentListItem(
            id = IdUtil.generateLongId(),
            version = 0,
            externalId = model.contentId,
            author = model.author,
            repliedAuthor = repliedAuthor,
            repliedCommentLevel = repliedCommentLevel,
            currentUserId = currentUserId,
            content = model.content.body.postBlock,
            voteBalance = model.votes.upCount - model.votes.downCount,
            isUpVoteActive = model.votes.hasUpVote,
            isDownVoteActive = model.votes.hasDownVote,
            metadata = model.meta,
            state = CommentListItemState.NORMAL
        )
    }
}