package io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source

import android.util.Log
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.FirstLevelCommentListItem
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.SecondLevelCommentListItem
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionAuthorModel
import io.golos.domain.interactors.model.DiscussionIdModel
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
            votes = model.votes,
            metadata = model.meta
        )
    }

    fun mapToSecondLevel(
        model: CommentModel,
        currentUserId: String,
        parentAuthors: Map<DiscussionIdModel, DiscussionAuthorModel>): SecondLevelCommentListItem {
        if(model.content.commentLevel != 1) {
            throw UnsupportedOperationException("This level of comment is not supported: ${model.content.commentLevel}")
        }

        return SecondLevelCommentListItem(
            id = IdUtil.generateLongId(),
            version = 0,
            externalId = model.contentId,
            author = model.author,
            parentAuthor = model.parentId?.let { parentAuthors[it] },
            currentUserId = currentUserId,
            content = model.content.body.postBlock,
            votes = model.votes,
            metadata = model.meta
        )
    }
}