package io.golos.cyber_android.ui.screens.profile.view.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet.model.CurrencyBalance

@BindingAdapter("profile_wallet_view_value")
fun setJoinedDateBinding(view: ProfileWalletView, valueToBind: LiveData<CurrencyBalance>?) =
    valueToBind?.value?.let { view.setValue(it) }
