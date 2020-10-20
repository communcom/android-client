package io.golos.cyber_android.ui.screens.profile_black_list.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.UserDomain

data class UserListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    val user: UserDomain,

    /**
     * In a black list (positive) / Not in a black list (negative)
     */
    val isInPositiveState: Boolean,

    val isProgress: Boolean
) : VersionedListItem