package io.golos.cyber_android.application.dependency_injection.login_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.ViewModelKey
import io.golos.cyber_android.ui.screens.login.AuthViewModel
import io.golos.cyber_android.ui.screens.login.signin.user_name.UserNameSignInViewModel
import io.golos.cyber_android.ui.screens.login.signup.SignUpViewModel
import io.golos.cyber_android.ui.screens.login.signup.country.SignUpCountryViewModel
import io.golos.cyber_android.ui.screens.login.signup.fingerprint.FingerprintModel
import io.golos.cyber_android.ui.screens.login.signup.fingerprint.FingerprintModelImpl
import io.golos.cyber_android.ui.screens.login.signup.fingerprint.FingerprintViewModel
import io.golos.cyber_android.ui.screens.login.signup.keys_backup.SignUpProtectionKeysViewModel
import io.golos.cyber_android.ui.screens.login.signup.pin.PinCodeModel
import io.golos.cyber_android.ui.screens.login.signup.pin.PinCodeModelImpl
import io.golos.cyber_android.ui.screens.login.signup.pin.PinCodeViewModel
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Module
abstract class LoginActivityModuleBinds {
    @Binds
    abstract fun providePinCodeModel(model: PinCodeModelImpl): PinCodeModel

    @Binds
    abstract fun provideFingerprintModel(model: FingerprintModelImpl): FingerprintModel

    @Binds
    @ActivityScope
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactory): ViewModelProvider.Factory

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
}