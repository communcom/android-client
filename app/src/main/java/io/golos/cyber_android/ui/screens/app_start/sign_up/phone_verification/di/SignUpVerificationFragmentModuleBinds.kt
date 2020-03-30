package io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.model.SignUpVerificationModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.model.SignUpVerificationModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.view_model.SignUpVerificationViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignUpVerificationFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignUpVerificationViewModel::class)
    abstract fun provideViewModel(viewModel: SignUpVerificationViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignUpVerificationModelImpl): SignUpVerificationModel
}