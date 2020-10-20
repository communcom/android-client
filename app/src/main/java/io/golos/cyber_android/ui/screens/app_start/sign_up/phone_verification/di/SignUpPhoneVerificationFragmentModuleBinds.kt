package io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.model.SignUpPhoneVerificationModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.model.SignUpPhoneVerificationModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.view_model.SignUpPhoneVerificationViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignUpPhoneVerificationFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignUpPhoneVerificationViewModel::class)
    abstract fun provideViewModel(viewModel: SignUpPhoneVerificationViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignUpPhoneVerificationModelImpl): SignUpPhoneVerificationModel
}