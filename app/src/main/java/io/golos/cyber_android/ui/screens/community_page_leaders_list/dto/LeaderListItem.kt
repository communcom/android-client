package io.golos.cyber_android.ui.screens.community_page_leaders_list.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.UserIdDomain

data class LeaderListItem(
    override val id: Long,
    override val version: Long,

    val userId: UserIdDomain,
    val avatarUrl: String?,
    val username: String,
    val rating: Double,
    val ratingPercent: Double,

    val isVoted: Boolean
): VersionedListItem