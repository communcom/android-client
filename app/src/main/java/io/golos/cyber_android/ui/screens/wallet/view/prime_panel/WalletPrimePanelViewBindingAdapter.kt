package io.golos.cyber_android.ui.screens.wallet.view.prime_panel

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("wallet_prime_panel_view_value")
fun setPrimePanelValueDateBinding(view: WalletPrimePanelView, valueToBind: LiveData<Double>?) =
    valueToBind?.value?.let { view.setValue(it) }