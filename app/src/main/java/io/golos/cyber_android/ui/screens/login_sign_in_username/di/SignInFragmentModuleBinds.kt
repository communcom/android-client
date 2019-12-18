package io.golos.cyber_android.ui.screens.login_sign_in_username.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.login_sign_in_username.model.SignInModel
import io.golos.cyber_android.ui.screens.login_sign_in_username.model.SignInModelImpl
import io.golos.cyber_android.ui.screens.login_sign_in_username.view_model.SignInViewModel

@Module
abstract class SignInFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignInViewModel::class)
    abstract fun provideProfileViewModel(viewModel: SignInViewModel): ViewModel

    @Binds
    abstract fun provideSignInModel(model: SignInModelImpl): SignInModel
}