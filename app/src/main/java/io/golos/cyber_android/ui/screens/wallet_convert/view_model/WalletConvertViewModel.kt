package io.golos.cyber_android.ui.screens.wallet_convert.view_model

import android.content.Context
import io.golos.cyber_android.ui.screens.wallet_convert.model.WalletConvertModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class WalletConvertViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: WalletConvertModel
) : ViewModelBase<WalletConvertModel>(dispatchersProvider, model) {

}