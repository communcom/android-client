package io.golos.cyber_android.ui.screens.wallet_shared.send_points.widgets

import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

@BindingAdapter("wallet_send_points_expanded_panel_data")
fun setWalletSendPointsExpandedTopPanelData(view: WalletSendPointsExpandedTopPanel, valueToBind: LiveData<WalletCommunityBalanceRecordDomain>?) =
    valueToBind?.value?.let { view.setData(it) }

@BindingAdapter("wallet_send_points_expanded_panel_carousel_start")
fun setWalletSendPointsExpandedTopPanelCarouselStart(view: WalletSendPointsExpandedTopPanel, valueToBind: LiveData<CarouselStartData>?) =
    valueToBind?.value?.let { view.setCarouselStartData(it) }

@BindingAdapter("wallet_send_points_expanded_panel_title")
fun setWalletSendPointsExpandedTopPanelTitle(view: WalletSendPointsExpandedTopPanel, @StringRes valueToBind: Int) {
    view.setTitle(valueToBind)
}

@BindingAdapter("wallet_send_points_expanded_panel_mode")
fun setWalletSendPointsExpandedTopPanelMode(view: WalletSendPointsExpandedTopPanel, valueToBind: LiveData<Boolean>?) =
    valueToBind?.value?.let { view.switchMode(it) }

@BindingAdapter("wallet_send_points_expanded_menu")
fun setWalletSendPointsExpandedTopPanelMenu(view: WalletSendPointsExpandedTopPanel, valueToBind: LiveData<Boolean>?) =
    valueToBind?.value?.let { view.setMenuVisibility(it) }