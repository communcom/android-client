package io.golos.cyber_android.ui.screens.communities_list.dto

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.CommunityDomain

data class CommunityListItem(
    override val id: Long,
    override val version: Long,
    val community: CommunityDomain,

    val isJoined: Boolean,
    val isProgress: Boolean
) : VersionedListItem