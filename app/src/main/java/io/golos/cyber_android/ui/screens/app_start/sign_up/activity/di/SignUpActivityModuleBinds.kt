package io.golos.cyber_android.ui.screens.app_start.sign_up.activity.di

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.vizualizer.UserNameValidationVisualizer
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.vizualizer.UserNameValidationVisualizerImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.visializer.PasswordValidationVisualizer
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.visializer.PasswordValidationVisualizerImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.data_pass.SignUpFragmentsDataPass
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.data_pass.SignUpFragmentsDataPassImpl
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapperImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactoryImpl
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.validation.user_name.UserNameValidator
import io.golos.domain.validation.user_name.UserNameValidatorImpl
import io.golos.use_cases.auth.AuthUseCase
import io.golos.use_cases.auth.AuthUseCaseImpl
import io.golos.use_cases.sign_up.core.SignUpCoreManagement
import io.golos.use_cases.sign_up.core.SignUpCoreImpl
import io.golos.use_cases.sign_up.core.SignUpCoreView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@Suppress("unused")
@Module
abstract class SignUpActivityModuleBinds {
    @Binds
    @ActivityScope
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactoryImpl): ActivityViewModelFactory

    @Binds
    abstract fun provideAuthUseCase(useCase: AuthUseCaseImpl): AuthUseCase

    /**
     * Validators
     */
    @Binds
    abstract fun provideUserNameValidator(validator: UserNameValidatorImpl): UserNameValidator

    @Binds
    abstract fun provideUserValidationVisualizer(visualizer: UserNameValidationVisualizerImpl): UserNameValidationVisualizer

    @Binds
    abstract fun providePasswordValidationVisualizer(visualizer: PasswordValidationVisualizerImpl): PasswordValidationVisualizer

    @Binds
    @ActivityScope
    abstract fun provideSignUpFragmentsDataPass(dataPass: SignUpFragmentsDataPassImpl): SignUpFragmentsDataPass

    @ExperimentalCoroutinesApi
    @FlowPreview
    @ActivityScope
    @Binds
    abstract fun provideSingUpCoreView(core: SignUpCoreImpl): SignUpCoreView

    @ExperimentalCoroutinesApi
    @FlowPreview
    @ActivityScope
    @Binds
    abstract fun provideSingUpCoreClosable(core: SignUpCoreImpl): SignUpCoreManagement

    @Binds
    abstract fun provideSingUpMessageMapper(mapper: SignUpMessagesMapperImpl): SignUpMessagesMapper
}