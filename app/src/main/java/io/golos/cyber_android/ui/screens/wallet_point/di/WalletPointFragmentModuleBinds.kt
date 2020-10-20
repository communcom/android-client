package io.golos.cyber_android.ui.screens.wallet_point.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.wallet_point.model.WalletPointModel
import io.golos.cyber_android.ui.screens.wallet_point.model.WalletPointModelImpl
import io.golos.cyber_android.ui.screens.wallet_point.view_model.WalletPointViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class WalletPointFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(WalletPointViewModel::class)
    abstract fun provideWalletPointViewModel(viewModel: WalletPointViewModel): ViewModel

    @Binds
    abstract fun provideWalletPointModel(model: WalletPointModelImpl): WalletPointModel
}