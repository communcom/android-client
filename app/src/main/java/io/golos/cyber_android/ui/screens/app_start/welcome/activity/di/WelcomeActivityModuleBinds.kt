package io.golos.cyber_android.ui.screens.app_start.welcome.activity.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.model.WelcomeModel
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.model.WelcomeModelImpl
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.view_model.WelcomeViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.use_cases.auth.AuthUseCase
import io.golos.use_cases.auth.AuthUseCaseImpl

@Suppress("unused")
@Module
abstract class WelcomeActivityModuleBinds {
    @Binds
    @ActivityScope
    abstract fun bindViewModelFactory(factory: ActivityViewModelFactoryImpl): ActivityViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    abstract fun provideLoginViewModel(model: WelcomeViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: WelcomeModelImpl): WelcomeModel

    @Binds
    abstract fun provideAuthUseCase(useCase: AuthUseCaseImpl): AuthUseCase
}