package io.golos.cyber_android.ui.screens.app_start.sign_in.activity.di

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.validator.UserNameValidator
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.validator.UserNameValidatorImpl
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.vizualizer.UserNameValidationVisualizer
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.vizualizer.UserNameValidationVisualizerImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.shared.data_pass.SignInFragmentsDataPass
import io.golos.cyber_android.ui.screens.app_start.sign_in.shared.data_pass.SignInFragmentsDataPassImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.validator.PasswordValidator
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.validator.PasswordValidatorImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.visializer.PasswordValidationVisualizer
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.visializer.PasswordValidationVisualizerImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactoryImpl
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.use_cases.auth.AuthUseCase
import io.golos.use_cases.auth.AuthUseCaseImpl

@Suppress("unused")
@Module
abstract class SignInActivityModuleBinds {
    @Binds
    @ActivityScope
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactoryImpl): ActivityViewModelFactory

    @Binds
    abstract fun provideAuthUseCase(useCase: AuthUseCaseImpl): AuthUseCase

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
    abstract fun provideSignInFragmentsDataPass(dataPass: SignInFragmentsDataPassImpl): SignInFragmentsDataPass
}