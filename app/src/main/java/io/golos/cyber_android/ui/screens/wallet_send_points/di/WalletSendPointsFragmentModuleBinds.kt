package io.golos.cyber_android.ui.screens.wallet_send_points.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModel
import io.golos.cyber_android.ui.screens.wallet_send_points.model.WalletSendPointsModelImpl
import io.golos.cyber_android.ui.screens.wallet_send_points.view_model.WalletSendPointsViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class WalletSendPointsFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(WalletSendPointsViewModel::class)
    abstract fun provideWalletSendPointsViewModel(viewModel: WalletSendPointsViewModel): ViewModel

    @Binds
    abstract fun provideWalletSendPointsModel(model: WalletSendPointsModelImpl): WalletSendPointsModel
}