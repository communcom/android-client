package io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.on_boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.onboardingImage.OnboardingUserImageViewModel
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.interactors.user.UserMetadataUseCaseImpl
import javax.inject.Named

@Module
abstract class OnBoardingFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(OnboardingUserImageViewModel::class)
    abstract fun provideOnboardingUserImageViewModel(viewModel: OnboardingUserImageViewModel): ViewModel

    @Binds
    abstract fun provideUserMetadataUseCase(useCase: UserMetadataUseCaseImpl): UserMetadataUseCase

    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory
}