package io.golos.cyber_android.ui.shared.recycler_view.versioned

import io.golos.domain.dto.CommunityDomain

data class CommunityListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    val community: CommunityDomain,

    /**
     * Subscribed (positive) / Unsubscribed (negative)
     * In a black list (positive) / Not in a black list (negative)
     */
    val isInPositiveState: Boolean,
    val isInBlockList:Boolean,
    val isProgress: Boolean
) : VersionedListItem