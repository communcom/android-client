package io.golos.cyber_android.ui.screens.app_start.sign_up.phone.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.model.SignUpPhoneModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.model.SignUpPhoneModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.view_model.SignUpPhoneViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignUpPhoneFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignUpPhoneViewModel::class)
    abstract fun provideViewModel(viewModel: SignUpPhoneViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignUpPhoneModelImpl): SignUpPhoneModel
}