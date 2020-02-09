package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.cyber_android.ui.shared.recycler_view.GroupListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.use_cases.model.DiscussionIdModel

data class SecondLevelCommentCollapsedListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    val totalChild: Long,
    val parentCommentId: DiscussionIdModel,
    override val groupId: Int = 5
) : GroupListItem, VersionedListItem