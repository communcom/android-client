package io.golos.cyber_android.ui.screens.profile_followers.dto

import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.domain.dto.UserDomain

data class FollowersListItem(
    override val id: Long,
    override val version: Long,
    val follower: UserDomain,

    val isJoined: Boolean,
    val isProgress: Boolean,
    val filter: FollowersFilter,

    val isLastItem: Boolean
) : VersionedListItem