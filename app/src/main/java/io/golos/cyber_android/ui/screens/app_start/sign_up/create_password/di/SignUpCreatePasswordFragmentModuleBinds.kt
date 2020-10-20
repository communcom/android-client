package io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.model.SignUpCreatePasswordModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.model.SignUpCreatePasswordModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.model.password_validator.PasswordValidator
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.model.password_validator.PasswordValidatorImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.view_model.SignUpCreatePasswordViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignUpCreatePasswordFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignUpCreatePasswordViewModel::class)
    abstract fun provideViewModel(viewModel: SignUpCreatePasswordViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignUpCreatePasswordModelImpl): SignUpCreatePasswordModel

    @Binds
    abstract fun providePasswordValidator(validator: PasswordValidatorImpl): PasswordValidator
}