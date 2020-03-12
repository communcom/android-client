package io.golos.cyber_android.ui.screens.post_view.dto.post_list_items

import io.golos.cyber_android.ui.shared.recycler_view.GroupListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

/**
 * Voting etc.
 */
data class PostControlsListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    val voteBalance: Long,
    val isUpVoteActive: Boolean,
    val isDownVoteActive: Boolean,

    val totalComments: Int,
    val totalViews: Int,
    override val groupId: Int = 2
) : GroupListItem, VersionedListItem