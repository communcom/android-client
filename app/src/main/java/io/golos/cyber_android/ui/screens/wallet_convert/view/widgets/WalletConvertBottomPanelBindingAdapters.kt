package io.golos.cyber_android.ui.screens.wallet_convert.view.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet_convert.dto.ConvertButtonInfo
import io.golos.cyber_android.ui.screens.wallet_convert.dto.ErrorLabelInfo
import io.golos.cyber_android.ui.screens.wallet_convert.dto.InputFieldsInfo
import io.golos.cyber_android.ui.screens.wallet_convert.dto.PointInfo

@BindingAdapter("wallet_convert_bottom_point_info")
fun setWalletConvertBottomPanelPointInfo(view: WalletConvertBottomPanel, valueToBind: LiveData<PointInfo>?) {
    valueToBind?.value?.let { view.setPointInfo(it) }
}

@BindingAdapter("wallet_convert_bottom_input_info")
fun setWalletConvertBottomPanelInputInfo(view: WalletConvertBottomPanel, valueToBind: LiveData<InputFieldsInfo>?) {
    valueToBind?.value?.let { view.setInputFieldsInfo(it) }
}

@BindingAdapter("wallet_convert_bottom_button_info")
fun setWalletConvertBottomPanelConvertButtonInfo(view: WalletConvertBottomPanel, valueToBind: LiveData<ConvertButtonInfo>?) {
    valueToBind?.value?.let { view.setConvertButtonInfo(it) }
}

@BindingAdapter("wallet_convert_bottom_error_info")
fun setWalletConvertBottomPanelErrorInfo(view: WalletConvertBottomPanel, valueToBind: LiveData<ErrorLabelInfo>?) {
    valueToBind?.value?.let { view.setErrorLabelInfo(it) }
}

@BindingAdapter("wallet_convert_bottom_sell_field")
fun setWalletConvertBottomPanelSell(view: WalletConvertBottomPanel, valueToBind: LiveData<String>?) {
    valueToBind?.value?.let { view.sellInputText = it }
}

@BindingAdapter("wallet_convert_bottom_buy_field")
fun setWalletConvertBottomPanelBuy(view: WalletConvertBottomPanel, valueToBind: LiveData<String>?) {
    valueToBind?.value?.let { view.buyInputText = it }
}