package io.golos.cyber_android.ui.screens.app_start.sign_up.pin.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.pin_code_storage.PinCodesStorage
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.pin_code_storage.PinCodesStorageImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.pin.model.SignUpPinCodeModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.pin.model.SignUpPinCodeModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.pin.view_model.SignUpPinCodeViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignUpPinCodeFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignUpPinCodeViewModel::class)
    abstract fun provideViewModel(viewModel: SignUpPinCodeViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignUpPinCodeModelImpl): SignUpPinCodeModel

    @Binds
    abstract fun provideCodesStorage(storage: PinCodesStorageImpl): PinCodesStorage
}