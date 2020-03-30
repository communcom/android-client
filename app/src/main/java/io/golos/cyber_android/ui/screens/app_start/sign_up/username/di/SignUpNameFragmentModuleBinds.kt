package io.golos.cyber_android.ui.screens.app_start.sign_up.username.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.model.SignUpNameModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.model.SignUpNameModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.view_model.SignUpNameViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignUpNameFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignUpNameViewModel::class)
    abstract fun provideViewModel(viewModel: SignUpNameViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignUpNameModelImpl): SignUpNameModel
}