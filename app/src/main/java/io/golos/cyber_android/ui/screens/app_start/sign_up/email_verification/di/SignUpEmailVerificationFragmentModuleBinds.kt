package io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.model.SignUpEmailVerificationModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.model.SignUpEmailVerificationModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.view_model.SignUpEmailVerificationViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class SignUpEmailVerificationFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignUpEmailVerificationViewModel::class)
    abstract fun provideViewModel(viewModel: SignUpEmailVerificationViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignUpEmailVerificationModelImpl): SignUpEmailVerificationModel
}