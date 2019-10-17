package io.golos.cyber_android.ui.shared_fragments.post.view.list.list_items

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

/**
 * Voting etc.
 */
data class PostControlsListItem(
    override val id: Long,
    override val version: Long,

    val voteBalance: Long,

    val isUpVoteActive: Boolean,
    val isDownVoteActive: Boolean,

    val totalComments: Long,
    val totalViews: Long
) : VersionedListItem