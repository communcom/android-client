package io.golos.cyber_android.ui.screens.wallet.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.wallet.model.WalletModel
import io.golos.cyber_android.ui.screens.wallet.model.WalletModelImpl
import io.golos.cyber_android.ui.screens.wallet_shared.history.data_source.HistoryDataSource
import io.golos.cyber_android.ui.screens.wallet_shared.history.data_source.HistoryDataSourceImpl
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.data_source.SendPointsDataSource
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.data_source.SendPointsDataSourceImpl
import io.golos.cyber_android.ui.screens.wallet.view_model.WalletViewModel
import io.golos.cyber_android.ui.screens.wallet_shared.amount_validator.AmountValidator
import io.golos.cyber_android.ui.screens.wallet_shared.amount_validator.AmountValidatorImpl
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
    abstract fun provideListWorkerSendPoints(worker: SendPointsDataSourceImpl): SendPointsDataSource

    @Binds
    abstract fun provideListWorkerHistory(worker: HistoryDataSourceImpl): HistoryDataSource

    @Binds
    abstract fun provideAmountValidator(validator: AmountValidatorImpl): AmountValidator
}