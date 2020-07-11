package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.cyber_android.ui.shared.recycler_view.GroupListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.MetaDomain
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ContentBlock

interface CommentListItem : GroupListItem, VersionedListItem {
    val externalId: ContentIdDomain          // Id of an entity on the backend

    val author: UserBriefDomain
    val currentUserId: UserIdDomain

    val content: ContentBlock?

    val voteBalance: Long
    val isUpVoteActive: Boolean
    val isDownVoteActive: Boolean

    val metadata: MetaDomain

    val state: CommentListItemState

    val isDeleted: Boolean

}