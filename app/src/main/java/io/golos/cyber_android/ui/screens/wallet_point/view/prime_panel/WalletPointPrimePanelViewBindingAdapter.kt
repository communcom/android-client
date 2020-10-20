package io.golos.cyber_android.ui.screens.wallet_point.view.prime_panel

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData

@BindingAdapter("wallet_point_prime_panel_title")
fun setWalletPointPrimePanelTitleDateBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<String>?) =
    valueToBind?.value?.let { view.setTitle(it) }

@BindingAdapter("wallet_point_prime_panel_value")
fun setWalletPointPrimePanelValueDateBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<Double>?) =
    valueToBind?.value?.let { view.setValue(it) }

@BindingAdapter("wallet_point_prime_panel_value_communs")
fun setWalletPointPrimePanelValueInCommunsDateBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<Double>?) =
    valueToBind?.value?.let { view.setValueCommun(it) }

@BindingAdapter("wallet_point_prime_panel_available_hold_bar")
fun setWalletPointPrimePanelAvailableHoldBarDateBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<Double>?) =
    valueToBind?.value?.let { view.setAvailableHoldBarValue(it) }

@BindingAdapter("wallet_point_prime_panel_available")
fun setWalletPointPrimePanelAvailableBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<Double>?) =
    valueToBind?.value?.let { view.setAvailableValue(it) }

@BindingAdapter("wallet_point_prime_panel_hold")
fun setWalletPointPrimePaneHoldBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<Double>?) =
    valueToBind?.value?.let { view.setHoldValue(it) }

@BindingAdapter("wallet_point_prime_panel_carousel_start")
fun setWalletPointPrimePanelCarouselStartBinding(view: WalletPointPrimePanelView, valueToBind: LiveData<CarouselStartData>?) =
    valueToBind?.value?.let { view.setCarouselStartData(it) }