package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.DiscussionMetadataModel
import io.golos.domain.use_cases.post.post_dto.ContentBlock

data class SecondLevelCommentListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    override val externalId: DiscussionIdModel,          // Id of an entity on the backend

    override val author: DiscussionAuthorModel,
    val repliedAuthor: DiscussionAuthorModel?,
    override val currentUserId: String,
    val repliedCommentLevel: Int,

    override val content: ContentBlock?,

    override val voteBalance: Long,
    override val isUpVoteActive: Boolean,
    override val isDownVoteActive: Boolean,

    override val metadata: DiscussionMetadataModel,

    override val state: CommentListItemState,
    override val groupId: Int = 5,
    override val isDeleted: Boolean
) : CommentListItem