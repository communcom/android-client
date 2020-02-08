package io.golos.cyber_android.ui.screens.wallet.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.wallet.model.WalletModel
import io.golos.cyber_android.ui.screens.wallet.model.WalletModelImpl
import io.golos.cyber_android.ui.screens.wallet.model.lists_workers.ListWorkerSendPoints
import io.golos.cyber_android.ui.screens.wallet.model.lists_workers.ListWorkerSendPointsImpl
import io.golos.cyber_android.ui.screens.wallet.view_model.WalletViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class WalletFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(WalletViewModel::class)
    abstract fun provideWalletViewModel(viewModel: WalletViewModel): ViewModel

    @Binds
    abstract fun provideWalletModel(model: WalletModelImpl): WalletModel

    @Binds
    abstract fun provideListWorkerSendPoints(worker: ListWorkerSendPointsImpl): ListWorkerSendPoints
}