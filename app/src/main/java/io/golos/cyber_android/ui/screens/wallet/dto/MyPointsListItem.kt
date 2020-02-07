package io.golos.cyber_android.ui.screens.wallet.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

data class MyPointsListItem(
    override val id: Long,
    override val version: Long,

    val isCommun: Boolean,
    val data: WalletCommunityBalanceRecordDomain
): VersionedListItem
