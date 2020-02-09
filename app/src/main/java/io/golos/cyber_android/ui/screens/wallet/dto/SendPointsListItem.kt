package io.golos.cyber_android.ui.screens.wallet.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.UserIdDomain

data class SendPointsListItem(
    override val id: Long,
    override val version: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    val userId: UserIdDomain,
    val name: String,
    val avatarUrl: String?
): VersionedListItem
