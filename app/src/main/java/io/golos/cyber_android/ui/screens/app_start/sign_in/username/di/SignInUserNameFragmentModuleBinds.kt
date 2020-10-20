package io.golos.cyber_android.ui.screens.app_start.sign_in.username.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.SignInUserNameModel
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.SignInUserNameModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.view_model.SignInUserNameViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

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
}