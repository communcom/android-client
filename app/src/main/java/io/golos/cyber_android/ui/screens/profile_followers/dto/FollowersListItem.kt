package io.golos.cyber_android.ui.screens.profile_followers.dto

import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.domain.dto.UserDomain

data class FollowersListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    val follower: UserDomain,

    override val isFollowing: Boolean,
    val filter: FollowersFilter
) : UserListItem<FollowersListItem> {

    override fun updateIsFollowing(value: Boolean): FollowersListItem = copy(version = this.version + 1, isFollowing = value )

    override fun updateIsLastItem(value: Boolean): FollowersListItem = copy(version = this.version + 1, isLastItem = value )
}