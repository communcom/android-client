package io.golos.cyber_android.ui.screens.profile_followers.dto

import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.domain.dto.UserDomain

data class FollowersListItem(
    override val id: Long,
    override val version: Long,
    val follower: UserDomain,

    override val isFollowing: Boolean,
    val filter: FollowersFilter,

    override val isLastItem: Boolean
) : UserListItem<FollowersListItem> {

    override fun updateIsFollowing(value: Boolean): FollowersListItem = copy(version = this.version + 1, isFollowing = value )

    override fun updateIsLastItem(value: Boolean): FollowersListItem = copy(version = this.version + 1, isLastItem = value )
}