package io.golos.cyber_android.ui.screens.wallet_convert.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.wallet_convert.model.WalletConvertModel
import io.golos.cyber_android.ui.screens.wallet_convert.model.WalletConvertModelImpl
import io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator.AmountCalculator
import io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator.AmountCalculatorImpl
import io.golos.cyber_android.ui.screens.wallet_convert.view_model.WalletConvertViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class WalletConvertFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(WalletConvertViewModel::class)
    abstract fun provideWalletConvertViewModel(viewModel: WalletConvertViewModel): ViewModel

    @Binds
    abstract fun provideWalletConvertModel(model: WalletConvertModelImpl): WalletConvertModel

    @Binds
    abstract fun provideAmountCalculator(calculator: AmountCalculatorImpl): AmountCalculator
}