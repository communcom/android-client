package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.cyber_android.ui.shared.recycler_view.GroupListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.DiscussionMetadataModel
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ContentBlock

interface CommentListItem : GroupListItem, VersionedListItem {
    val externalId: DiscussionIdModel          // Id of an entity on the backend

    val author: DiscussionAuthorModel
    val currentUserId: String

    val content: ContentBlock?

    val voteBalance: Long
    val isUpVoteActive: Boolean
    val isDownVoteActive: Boolean

    val metadata: DiscussionMetadataModel

    val state: CommentListItemState

    val isDeleted: Boolean

}