package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.SignInPinCodeModel
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.SignInPinCodeModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.pin_code_storage.PinCodesStorage
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.pin_code_storage.PinCodesStorageImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.view_model.SignInPinCodeViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignInPinCodeFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignInPinCodeViewModel::class)
    abstract fun provideViewModel(viewModel: SignInPinCodeViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignInPinCodeModelImpl): SignInPinCodeModel

    @Binds
    abstract fun provideCodesStorage(storage: PinCodesStorageImpl): PinCodesStorage
}