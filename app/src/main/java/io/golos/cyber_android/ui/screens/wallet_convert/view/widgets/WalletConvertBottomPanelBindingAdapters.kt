package io.golos.cyber_android.ui.screens.wallet_convert.view.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.screens.wallet_convert.dto.ConvertButtonInfo
import io.golos.cyber_android.ui.screens.wallet_convert.dto.InputFieldsInfo
import io.golos.cyber_android.ui.screens.wallet_convert.dto.PointInfo
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.AmountFieldInfo
import io.golos.cyber_android.ui.shared.extensions.parentActivity

@BindingAdapter("wallet_convert_bottom_point_info")
fun setWalletConvertBottomPanelPointInfo(view: WalletConvertBottomPanel, valueToBind: LiveData<PointInfo>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setPointInfo(it) })
        }
    }
}

@BindingAdapter("wallet_convert_bottom_input_info")
fun setWalletConvertBottomPanelInputInfo(view: WalletConvertBottomPanel, valueToBind: LiveData<InputFieldsInfo>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setInputFieldsInfo(it) })
        }
    }
}

@BindingAdapter("wallet_convert_bottom_button_info")
fun setWalletConvertBottomPanelConvertButtonInfo(view: WalletConvertBottomPanel, valueToBind: LiveData<ConvertButtonInfo>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setConvertButtonInfo(it) })
        }
    }
}

@BindingAdapter("wallet_convert_bottom_sell_field")
fun setWalletConvertBottomPanelSell(view: WalletConvertBottomPanel, valueToBind: MutableLiveData<String>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer {
                if(it != view.sellInputText) {
                    view.sellInputText = it
                }
            })
        }

        view.setOnSellChangeListener { valueToBind.value = it }
    }
}

@BindingAdapter("wallet_convert_bottom_buy_field")
fun setWalletConvertBottomPanelBuy(view: WalletConvertBottomPanel, valueToBind: MutableLiveData<String>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer {
                if(it != view.buyInputText) {
                    view.buyInputText = it
                }
            })
        }

        view.setOnSellChangeListener { valueToBind.value = it }
    }
}