package io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.UserBriefDomain

data class SendPointsListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    val user: UserBriefDomain
): VersionedListItem
