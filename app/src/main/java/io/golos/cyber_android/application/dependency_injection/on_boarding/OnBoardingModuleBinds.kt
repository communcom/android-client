package io.golos.cyber_android.application.dependency_injection.on_boarding

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.ViewModelKey
import io.golos.cyber_android.ui.screens.login.signup.onboarding.image.OnboardingUserImageViewModel
import io.golos.cyber_android.ui.screens.profile.edit.bio.EditProfileBioViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.user.UserMetadataUseCase

@Module
abstract class OnBoardingModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(EditProfileBioViewModel::class)
    abstract fun provideEditProfileBioViewModel(viewModel: EditProfileBioViewModel): EditProfileBioViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingUserImageViewModel::class)
    abstract fun provideOnboardingUserImageViewModel(viewModel: OnboardingUserImageViewModel): OnboardingUserImageViewModel

    @Binds
    abstract fun provideUserMetadataUseCase(useCase: UserMetadataUseCase): UserMetadataUseCase

    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory
}