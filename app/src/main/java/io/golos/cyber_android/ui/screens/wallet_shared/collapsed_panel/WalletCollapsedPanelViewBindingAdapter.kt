package io.golos.cyber_android.ui.screens.wallet_shared.collapsed_panel

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet.model.CurrencyBalance

@BindingAdapter("wallet_collapsed_panel_view_value")
fun setCollapsedPanelValueDateBinding(view: WalletCollapsedPanelView, valueToBind: LiveData<CurrencyBalance>?) =
    valueToBind?.value?.let { view.setValue(it) }

@BindingAdapter("wallet_collapsed_panel_view_title")
fun setCollapsedPanelTitleDateBinding(view: WalletCollapsedPanelView, valueToBind: LiveData<String>?) =
    valueToBind?.value?.let { view.setTitle(it) }
