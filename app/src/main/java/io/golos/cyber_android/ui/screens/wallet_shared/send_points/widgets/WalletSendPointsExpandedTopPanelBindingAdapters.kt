package io.golos.cyber_android.ui.screens.wallet_shared.send_points.widgets

import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.shared.extensions.parentActivity
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

@BindingAdapter("wallet_send_points_expanded_panel_data")
fun setWalletSendPointsExpandedTopPanelData(
    view: WalletSendPointsExpandedTopPanel,
    valueToBind: LiveData<WalletCommunityBalanceRecordDomain>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setData(it) })
        }
    }
}

@BindingAdapter("wallet_send_points_expanded_panel_carousel_start")
fun setWalletSendPointsExpandedTopPanelCarouselStart(view: WalletSendPointsExpandedTopPanel, valueToBind: LiveData<CarouselStartData>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setCarouselStartData(it) })
        }
    }
}

@BindingAdapter("wallet_send_points_expanded_panel_title")
fun setWalletSendPointsExpandedTopPanelTitle(view: WalletSendPointsExpandedTopPanel, @StringRes valueToBind: Int) {
    view.setTitle(valueToBind)
}

@BindingAdapter("wallet_send_points_expanded_panel_mode")
fun setWalletSendPointsExpandedTopPanelMode(view: WalletSendPointsExpandedTopPanel, valueToBind: LiveData<Boolean>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.switchMode(it) })
        }
    }
}

@BindingAdapter("wallet_send_points_expanded_menu")
fun setWalletSendPointsExpandedTopPanelMenu(view: WalletSendPointsExpandedTopPanel, valueToBind: LiveData<Boolean>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setMenuVisibility(it) })
        }
    }
}