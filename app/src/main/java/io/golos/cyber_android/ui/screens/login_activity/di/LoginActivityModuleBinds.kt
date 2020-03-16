package io.golos.cyber_android.ui.screens.login_activity.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.login_activity.model.LoginModel
import io.golos.cyber_android.ui.screens.login_activity.model.LoginModelImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.login_activity.view_model.LoginViewModel
import io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass.LoginActivityFragmentsDataPassImpl
import io.golos.cyber_android.ui.screens.login_shared.validators.password.validator.PasswordValidator
import io.golos.cyber_android.ui.screens.login_shared.validators.password.validator.PasswordValidatorImpl
import io.golos.cyber_android.ui.screens.login_shared.validators.password.visializer.PasswordValidationVisualizer
import io.golos.cyber_android.ui.screens.login_shared.validators.password.visializer.PasswordValidationVisualizerImpl
import io.golos.cyber_android.ui.screens.login_shared.validators.user_name.validator.UserNameValidator
import io.golos.cyber_android.ui.screens.login_shared.validators.user_name.validator.UserNameValidatorImpl
import io.golos.cyber_android.ui.screens.login_shared.validators.user_name.vizualizer.UserNameValidationVisualizer
import io.golos.cyber_android.ui.screens.login_shared.validators.user_name.vizualizer.UserNameValidationVisualizerImpl
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint.FingerprintModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint.FingerprintModelImpl
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint.FingerprintViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.name.SignUpNameViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.phone.SignUpPhoneViewModel
import io.golos.cyber_android.ui.screens.login_sign_up_pin.PinCodeModel
import io.golos.cyber_android.ui.screens.login_sign_up_pin.PinCodeModelImpl
import io.golos.cyber_android.ui.screens.login_sign_up_pin.PinCodeViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.verification.SignUpVerificationViewModel
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.use_cases.auth.AuthUseCase
import io.golos.use_cases.auth.AuthUseCaseImpl

@Suppress("unused")
@Module
abstract class LoginActivityModuleBinds {
    @Binds
    abstract fun providePinCodeModel(model: PinCodeModelImpl): PinCodeModel

    @Binds
    abstract fun provideFingerprintModel(model: FingerprintModelImpl): FingerprintModel

    @Binds
    @ActivityScope
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactoryImpl): ActivityViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun provideLoginViewModel(model: LoginViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: LoginModelImpl): LoginModel

    @Binds
    abstract fun provideAuthUseCase(useCase: AuthUseCaseImpl): AuthUseCase

    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel::class)
    @ActivityScope
    abstract fun provideSignUpViewModel(model: SignUpViewModel): ViewModel

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