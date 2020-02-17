package io.golos.cyber_android.ui.screens.wallet_send_points.view_model

import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class WalletSendPointsViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: WalletSendPointsModel
) : ViewModelBase<WalletSendPointsModel>(dispatchersProvider, model) {

    init {
//        loadPage(false)
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }
}