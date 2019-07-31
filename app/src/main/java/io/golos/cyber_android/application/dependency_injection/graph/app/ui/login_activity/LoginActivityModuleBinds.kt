package io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.login_activity.AuthViewModel
import io.golos.cyber_android.ui.screens.login_activity.signin.user_name.UserNameSignInViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.SignUpViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country.SignUpCountryViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country.model.SignUpCountryModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country.model.SignUpCountryModelImpl
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.fingerprint.FingerprintModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.fingerprint.FingerprintModelImpl
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.fingerprint.FingerprintViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.keys_backup.SignUpProtectionKeysViewModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin.PinCodeModel
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin.PinCodeModelImpl
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin.PinCodeViewModel
import io.golos.domain.dependency_injection.scopes.ActivityScope

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