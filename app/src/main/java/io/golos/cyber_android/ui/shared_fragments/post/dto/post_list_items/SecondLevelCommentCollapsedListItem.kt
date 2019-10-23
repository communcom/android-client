package io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.interactors.model.DiscussionAuthorModel
import io.golos.domain.interactors.model.DiscussionIdModel

data class SecondLevelCommentCollapsedListItem(
    override val id: Long,
    override val version: Long,

    val topCommentAuthor: DiscussionAuthorModel,
    val currentUserId: String,

    val totalChild: Long,

    val parentCommentId: DiscussionIdModel
) : VersionedListItem