package io.golos.cyber_android.ui.screens.community_page_leaders_list.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.UserIdDomain

data class LeaderListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean = false,
    override val isLastItem: Boolean = false,

    val userId: UserIdDomain,
    val avatarUrl: String?,
    val username: String,
    val rating: Double,
    val ratingPercent: Double,
    val isVoted: Boolean,
    val isTop: Boolean
): VersionedListItem