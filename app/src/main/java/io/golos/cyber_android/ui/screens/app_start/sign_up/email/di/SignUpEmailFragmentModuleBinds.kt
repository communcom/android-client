package io.golos.cyber_android.ui.screens.app_start.sign_up.email.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_up.email.model.SignUpEmailModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.email.model.SignUpEmailModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.email.view_model.SignUpEmailViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignUpEmailFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignUpEmailViewModel::class)
    abstract fun provideViewModel(viewModel: SignUpEmailViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignUpEmailModelImpl): SignUpEmailModel
}