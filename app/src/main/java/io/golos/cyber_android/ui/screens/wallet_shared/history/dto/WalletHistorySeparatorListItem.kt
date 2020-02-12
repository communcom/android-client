package io.golos.cyber_android.ui.screens.wallet_shared.history.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.utils.IdUtil

data class WalletHistorySeparatorListItem(
    override val id: Long = IdUtil.generateLongId(),
    override val version: Long = 0,
    override val isFirstItem: Boolean = false,
    override val isLastItem: Boolean = false,

    val type: WalletHistorySeparatorType,
    val days: Int? = null // If type is WalletHistorySeparatorType.DAYS_AGO only
): VersionedListItem