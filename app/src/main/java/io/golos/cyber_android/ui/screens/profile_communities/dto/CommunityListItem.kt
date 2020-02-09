package io.golos.cyber_android.ui.screens.profile_communities.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.Community

data class CommunityListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    val community: Community,

    val isJoined: Boolean,
    val isProgress: Boolean
) : VersionedListItem