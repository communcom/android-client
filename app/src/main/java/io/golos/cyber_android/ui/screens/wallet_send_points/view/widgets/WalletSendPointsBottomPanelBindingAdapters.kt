package io.golos.cyber_android.ui.screens.wallet_send_points.view.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.UserInfo
import io.golos.cyber_android.ui.shared.extensions.parentActivity

@BindingAdapter("wallet_send_points_bottom_user_info")
fun setWalletSendPointsBottomPanelUserInfo(view: WalletSendPointsBottomPanel, valueToBind: LiveData<UserInfo>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setUserInfo(it) })
        }
    }
}