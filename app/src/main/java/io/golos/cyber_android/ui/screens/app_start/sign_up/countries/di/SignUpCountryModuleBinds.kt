package io.golos.cyber_android.ui.screens.app_start.sign_up.countries.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_up.countries.model.SignUpCountryModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.countries.model.SignUpCountryModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.countries.view_model.SignUpCountryViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignUpCountryModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignUpCountryViewModel::class)
    abstract fun provideViewModel(viewModel: SignUpCountryViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignUpCountryModelImpl): SignUpCountryModel
}