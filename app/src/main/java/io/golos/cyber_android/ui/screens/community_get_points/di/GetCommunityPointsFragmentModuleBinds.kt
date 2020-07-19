package io.golos.cyber_android.ui.screens.community_get_points.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.wallet_convert.model.WalletConvertModel
import io.golos.cyber_android.ui.screens.wallet_convert.model.WalletConvertModelImpl
import io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator.AmountCalculator
import io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator.AmountCalculatorImpl
import io.golos.cyber_android.ui.screens.wallet_convert.view_model.WalletConvertViewModel
import io.golos.cyber_android.ui.screens.wallet_shared.amount_validator.AmountValidator
import io.golos.cyber_android.ui.screens.wallet_shared.amount_validator.AmountValidatorImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class GetCommunityPointsFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(WalletConvertViewModel::class)
    abstract fun providesWalletConvertPointsViewModel(viewModel: WalletConvertViewModel): ViewModel

    @Binds
    abstract fun provideDonateSendPointsModel(model: WalletConvertModelImpl): WalletConvertModel

    @Binds
    abstract fun provideAmountValidator(validator: AmountValidatorImpl): AmountValidator

    @Binds
    abstract fun provideAmountCalculator(calculator: AmountCalculatorImpl): AmountCalculator
}