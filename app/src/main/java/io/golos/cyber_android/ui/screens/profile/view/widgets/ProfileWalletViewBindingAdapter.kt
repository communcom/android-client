package io.golos.cyber_android.ui.screens.profile.view.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.shared.extensions.parentActivity

@BindingAdapter("profile_wallet_view_value")
fun setJoinedDateBinding(view: ProfileWalletView, valueToBind: LiveData<Double>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setValue(it) })
        }
    }
}
