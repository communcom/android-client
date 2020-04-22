package io.golos.cyber_android.ui.screens.community_page_members.dto

import io.golos.cyber_android.ui.screens.profile_followers.dto.UserListItem
import io.golos.domain.dto.UserDomain

data class CommunityUserListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean = false,
    override val isLastItem: Boolean = false,

    val user: UserDomain,

    override val isFollowing: Boolean,

    val canFollow: Boolean,

    val showPosts: Boolean,

    val showEmptyFollowersAnsPosts: Boolean
) : UserListItem<CommunityUserListItem> {
    override fun updateIsFollowing(value: Boolean): CommunityUserListItem = copy(version = this.version + 1, isFollowing = value)

    override fun updateIsLastItem(value: Boolean): CommunityUserListItem = copy(version = this.version + 1, isLastItem = value)
}