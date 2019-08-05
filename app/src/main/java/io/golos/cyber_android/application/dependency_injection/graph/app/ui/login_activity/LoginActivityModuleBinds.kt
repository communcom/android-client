package io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.login_activity.AuthViewModel
import io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.QrCodeSignInViewModel
import io.golos.cyber_android.ui.screens.login_activity.signin.user_name.UserNameSignInViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.SignUpViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country.SignUpCountryViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country.model.SignUpCountryModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country.model.SignUpCountryModelImpl
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.fingerprint.FingerprintModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.fingerprint.FingerprintModelImpl
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.fingerprint.FingerprintViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.keys_backup.SignUpProtectionKeysViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.name.SignUpNameViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.phone.SignUpPhoneViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin.PinCodeModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin.PinCodeModelImpl
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin.PinCodeViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.verification.SignUpVerificationViewModel
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Suppress("unused")
@Module
abstract class LoginActivityModuleBinds {
    @Binds
    abstract fun providePinCodeModel(model: PinCodeModelImpl): PinCodeModel

    @Binds
    abstract fun provideFingerprintModel(model: FingerprintModelImpl): FingerprintModel

    @Binds
    abstract fun provideSignUpCountryModel(model: SignUpCountryModelImpl): SignUpCountryModel

    @Binds
    @ActivityScope
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactoryImpl): ActivityViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun provideAuthViewModel(model: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserNameSignInViewModel::class)
    abstract fun provideUserNameSignInViewModel(model: UserNameSignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel::class)
    @ActivityScope
    abstract fun provideSignUpViewModel(model: SignUpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignUpCountryViewModel::class)
    abstract fun provideSignUpCountryViewModel(model: SignUpCountryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PinCodeViewModel::class)
    abstract fun providePinCodeViewModel(model: PinCodeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FingerprintViewModel::class)
    abstract fun provideFingerprintViewModel(model: FingerprintViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignUpProtectionKeysViewModel::class)
    abstract fun provideSignUpProtectionKeysViewModel(model: SignUpProtectionKeysViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(QrCodeSignInViewModel::class)
    abstract fun provideQrCodeSignInViewModel(model: QrCodeSignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignUpPhoneViewModel::class)
    abstract fun provideSignUpPhoneViewModel(model: SignUpPhoneViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignUpNameViewModel::class)
    abstract fun provideSignUpNameViewModel(model: SignUpNameViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignUpVerificationViewModel::class)
    abstract fun provideSignUpVerificationViewModel(model: SignUpVerificationViewModel): ViewModel
}