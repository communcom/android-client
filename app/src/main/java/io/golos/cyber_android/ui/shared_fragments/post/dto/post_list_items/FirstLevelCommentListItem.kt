package io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items

import io.golos.domain.interactors.model.DiscussionAuthorModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionMetadataModel
import io.golos.domain.interactors.model.DiscussionVotesModel
import io.golos.domain.post.post_dto.PostBlock

data class FirstLevelCommentListItem(
    override val id: Long,
    override val version: Long,

    override val externalId: DiscussionIdModel,          // Id of an entity on the backend

    override val author: DiscussionAuthorModel,
    override val currentUserId: String,

    override val content: PostBlock,

    override val votes: DiscussionVotesModel,
    override val metadata: DiscussionMetadataModel,

    override val state: CommentListItemState
) : CommentListItem