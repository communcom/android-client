package io.golos.cyber_android.ui.screens.wallet_shared.send_points.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

@BindingAdapter("wallet_send_points_collapsed_data")
fun setWalletSendPointsCollapsedTopPanelData(view: WalletSendPointsCollapsedTopPanel, valueToBind: LiveData<WalletCommunityBalanceRecordDomain>?) =
    valueToBind?.value?.let { view.setData(it) }

@BindingAdapter("wallet_send_points_collapsed_menu")
fun setWalletSendPointsCollapsedTopPanelMenu(view: WalletSendPointsCollapsedTopPanel, valueToBind: LiveData<Boolean>?) =
    valueToBind?.value?.let { view.setMenuVisibility(it) }