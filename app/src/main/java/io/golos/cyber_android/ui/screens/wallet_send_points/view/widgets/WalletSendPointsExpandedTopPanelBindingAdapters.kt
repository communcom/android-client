package io.golos.cyber_android.ui.screens.wallet_send_points.view.widgets

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