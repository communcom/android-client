package io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source

import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.CommentListItemState
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.FirstLevelCommentListItem
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.SecondLevelCommentListItem
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.utils.id.IdUtil

object CommentsMapper {
    fun mapToFirstLevel(model: CommentDomain, currentUserId: UserIdDomain): FirstLevelCommentListItem {
        if(model.commentLevel != 0) {
            throw UnsupportedOperationException("This level of comment is not supported: ${model.commentLevel}")
        }

        return FirstLevelCommentListItem(
            id = IdUtil.generateLongId(),
            version = 0,
            isFirstItem = false,
            isLastItem = false,
            externalId = model.contentId,
            author = model.author,
            currentUserId = currentUserId,
            content = model.body,
            voteBalance = model.votes.upCount - model.votes.downCount,
            isUpVoteActive = model.votes.hasUpVote,
            isDownVoteActive = model.votes.hasDownVote,
            metadata = model.meta,
            state = CommentListItemState.NORMAL,
            isDeleted = model.isDeleted,
            donations = model.donations,
            isMyComment = model.isMyComment
        )
    }

    fun mapToSecondLevel(
        model: CommentDomain,
        currentUserId: UserIdDomain,
        parentAuthors: Map<ContentIdDomain, UserBriefDomain>): SecondLevelCommentListItem {
        if(model.commentLevel != 1) {
            throw UnsupportedOperationException("This level of comment is not supported: ${model.commentLevel}")
        }

        val repliedAuthor = model.parent.comment?.let { parentAuthors[it] }
        val repliedCommentLevel = if(repliedAuthor == null) 0 else 1

        return mapToSecondLevel(model, currentUserId, repliedAuthor, repliedCommentLevel)
    }

    fun mapToSecondLevel(
        model: CommentDomain,
        currentUserId: UserIdDomain,
        repliedAuthor: UserBriefDomain?,
        repliedCommentLevel: Int): SecondLevelCommentListItem {
        if(model.commentLevel != 1) {
            throw UnsupportedOperationException("This level of comment is not supported: ${model.commentLevel}")
        }

        return SecondLevelCommentListItem(
            id = IdUtil.generateLongId(),
            version = 0,
            isFirstItem = false,
            isLastItem = false,
            externalId = model.contentId,
            author = model.author,
            repliedAuthor = repliedAuthor,
            repliedCommentLevel = repliedCommentLevel,
            currentUserId = currentUserId,
            content = model.body,
            voteBalance = model.votes.upCount - model.votes.downCount,
            isUpVoteActive = model.votes.hasUpVote,
            isDownVoteActive = model.votes.hasDownVote,
            metadata = model.meta,
            state = CommentListItemState.NORMAL,
            isDeleted = model.isDeleted,
            donations = model.donations,
            isMyComment = model.isMyComment
        )
    }
}