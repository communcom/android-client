package io.golos.cyber_android.ui.screens.login_sign_in_username.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.login_sign_in_username.model.SignInUserNameModel
import io.golos.cyber_android.ui.screens.login_sign_in_username.model.SignInUserNameModelImpl
import io.golos.cyber_android.ui.screens.login_sign_in_username.model.auth.AuthUseCase
import io.golos.cyber_android.ui.screens.login_sign_in_username.model.auth.AuthUseCaseImpl
import io.golos.cyber_android.ui.screens.login_sign_in_username.view_model.SignInUserNameViewModel

@Module
abstract class SignInUserNameFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignInUserNameViewModel::class)
    abstract fun provideProfileViewModel(viewModel: SignInUserNameViewModel): ViewModel

    @Binds
    abstract fun provideSignInModel(model: SignInUserNameModelImpl): SignInUserNameModel

    @Binds
    abstract fun provideAuthUseCase(useCase: AuthUseCaseImpl): AuthUseCase
}