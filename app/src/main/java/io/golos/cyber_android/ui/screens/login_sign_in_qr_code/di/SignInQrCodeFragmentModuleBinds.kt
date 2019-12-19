package io.golos.cyber_android.ui.screens.login_sign_in_qr_code.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.model.SignInQrCodeModel
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.model.SignInQrCodeModelImpl
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.view_model.SignInQrCodeViewModel

@Module
abstract class SignInQrCodeFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignInQrCodeViewModel::class)
    abstract fun provideProfileViewModel(viewModel: SignInQrCodeViewModel): ViewModel

    @Binds
    abstract fun provideSignInModel(model: SignInQrCodeModelImpl): SignInQrCodeModel
}