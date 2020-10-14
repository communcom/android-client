package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.PinCodeAuthViewModel
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.model.PinCodeAuthModel
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.model.PinCodeAuthModelImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class PinCodeAuthFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(PinCodeAuthViewModel::class)
    abstract fun providePinCodeAuthViewModel(viewModel: PinCodeAuthViewModel): ViewModel

    @Binds
    @FragmentScope
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    abstract fun provideModel(model: PinCodeAuthModelImpl): PinCodeAuthModel
}