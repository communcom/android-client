package io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.interactors.model.*
import io.golos.domain.post.post_dto.PostBlock

data class FirstLevelCommentListItem(
    override val id: Long,
    override val version: Long,

    val externalId: DiscussionIdModel,          // Id of an entity on the backend

    val author: DiscussionAuthorModel,
    val parentAuthor: DiscussionAuthorModel,
    val currentUserId: String,

    val content: PostBlock,

    val votes: DiscussionVotesModel,
    val metadata: DiscussionMetadataModel
) : VersionedListItem