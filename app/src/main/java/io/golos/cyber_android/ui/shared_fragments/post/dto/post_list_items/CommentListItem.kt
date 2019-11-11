package io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.DiscussionMetadataModel
import io.golos.domain.post.post_dto.PostBlock

interface CommentListItem : VersionedListItem {
    val externalId: DiscussionIdModel          // Id of an entity on the backend

    val author: DiscussionAuthorModel
    val currentUserId: String

    val content: PostBlock

    val voteBalance: Long
    val isUpVoteActive: Boolean
    val isDownVoteActive: Boolean

    val metadata: DiscussionMetadataModel

    val state: CommentListItemState
}