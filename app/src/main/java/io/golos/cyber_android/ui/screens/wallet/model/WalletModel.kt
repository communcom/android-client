package io.golos.cyber_android.ui.screens.wallet.model

import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface WalletModel : ModelBase {
    val totalBalance: Double

    suspend fun initBalance(needReload: Boolean)

    suspend fun getMyPointsItems(): List<MyPointsListItem>
}