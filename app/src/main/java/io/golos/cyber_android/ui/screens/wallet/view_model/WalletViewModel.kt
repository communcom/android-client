package io.golos.cyber_android.ui.screens.wallet.view_model

import io.golos.cyber_android.ui.screens.wallet.model.WalletModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class WalletViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: WalletModel
) : ViewModelBase<WalletModel>(dispatchersProvider, model) {

}