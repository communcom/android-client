package io.golos.cyber_android.ui.screens.profile.view.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("profile_wallet_view_value")
fun setJoinedDateBinding(view: ProfileWalletView, valueToBind: LiveData<Double>?) =
    valueToBind?.value?.let { view.setValue(it) }
