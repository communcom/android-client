package io.golos.cyber_android.ui.screens.wallet_shared.send_points.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.shared.extensions.parentActivity
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

@BindingAdapter("wallet_send_points_collapsed_data")
fun setWalletSendPointsCollapsedTopPanelData(
    view: WalletSendPointsCollapsedTopPanel,
    valueToBind: LiveData<WalletCommunityBalanceRecordDomain>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setData(it) })
        }
    }
}