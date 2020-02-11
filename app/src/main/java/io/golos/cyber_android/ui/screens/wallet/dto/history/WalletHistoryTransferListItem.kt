package io.golos.cyber_android.ui.screens.wallet.dto.history

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

data class WalletHistoryTransferListItem(
    override val id: Long,
    override val version: Long = 0,
    override val isFirstItem: Boolean = false,
    override val isLastItem: Boolean = false,

    val mainIcon: String?,
    val smallIcon: String?,
    val isSmallIconVisible: Boolean,

    val displayName: String,

    val type: WalletHistoryTransferType,

    val direction: WalletHistoryTransferDirection,

    val isOnHold: Boolean,

    val timeStamp: String,          // Formatted

    val coinsQuantity: Double,
    val coinsSymbol: String
): VersionedListItem