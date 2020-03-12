package io.golos.cyber_android.ui.screens.wallet_shared.collapsed_panel

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.shared.extensions.parentActivity

@BindingAdapter("wallet_collapsed_panel_view_value")
fun setCollapsedPanelValueDateBinding(view: WalletCollapsedPanelView, valueToBind: LiveData<Double>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setValue(it) })
        }
    }
}

@BindingAdapter("wallet_collapsed_panel_view_title")
fun setCollapsedPanelTitleDateBinding(view: WalletCollapsedPanelView, valueToBind: LiveData<String>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setTitle(it) })
        }
    }
}
