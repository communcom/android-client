package io.golos.cyber_android.ui.screens.login_activity.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.login_activity.AuthViewModel
import io.golos.cyber_android.ui.screens.login_activity.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_activity.fragments_data_pass.LoginActivityFragmentsDataPassImpl
import io.golos.cyber_android.ui.screens.login_activity.validators.password.validator.PasswordValidator
import io.golos.cyber_android.ui.screens.login_activity.validators.password.validator.PasswordValidatorImpl
import io.golos.cyber_android.ui.screens.login_activity.validators.password.visializer.PasswordValidationVisualizer
import io.golos.cyber_android.ui.screens.login_activity.validators.password.visializer.PasswordValidationVisualizerImpl
import io.golos.cyber_android.ui.screens.login_activity.validators.user_name.validator.UserNameValidator
import io.golos.cyber_android.ui.screens.login_activity.validators.user_name.validator.UserNameValidatorImpl
import io.golos.cyber_android.ui.screens.login_activity.validators.user_name.vizualizer.UserNameValidationVisualizer
import io.golos.cyber_android.ui.screens.login_activity.validators.user_name.vizualizer.UserNameValidationVisualizerImpl
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.country.SignUpCountryViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.country.model.SignUpCountryModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.country.model.SignUpCountryModelImpl
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint.FingerprintModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint.FingerprintModelImpl
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint.FingerprintViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.keys_backup.SignUpProtectionKeysViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.name.SignUpNameViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.phone.SignUpPhoneViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.pin.PinCodeModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.pin.PinCodeModelImpl
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.pin.PinCodeViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.verification.SignUpVerificationViewModel
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

    /**
     * Validators
     */
    @Binds
    abstract fun providePasswordValidator(validator: PasswordValidatorImpl): PasswordValidator

    @Binds
    abstract fun provideUserNameValidator(validator: UserNameValidatorImpl): UserNameValidator

    @Binds
    abstract fun provideUserValidationVisualizer(visualizer: UserNameValidationVisualizerImpl): UserNameValidationVisualizer

    @Binds
    abstract fun providePasswordValidationVisualizer(visualizer: PasswordValidationVisualizerImpl): PasswordValidationVisualizer

    @Binds
    @ActivityScope
    abstract fun provideLoginActivityFragmentsDataPass(dataPass: LoginActivityFragmentsDataPassImpl): LoginActivityFragmentsDataPass
}