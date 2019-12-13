package io.golos.cyber_android.ui.common.recycler_view.versioned

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.CommunityDomain

data class CommunityListItem(
    override val id: Long,
    override val version: Long,
    val community: CommunityDomain,

    /**
     * Subscribed (positive) / Unsubscribed (negative)
     * In a black list (positive) / Not in a black list (negative)
     */
    val isInPositiveState: Boolean,

    val isProgress: Boolean
) : VersionedListItem