package io.golos.cyber_android.ui.screens.profile_followers.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

interface UserListItem<T : UserListItem<T>> : VersionedListItem {
    val isFollowing: Boolean
    val isLastItem: Boolean

    fun updateIsFollowing(value: Boolean): T

    fun updateIsLastItem(value: Boolean): T
}