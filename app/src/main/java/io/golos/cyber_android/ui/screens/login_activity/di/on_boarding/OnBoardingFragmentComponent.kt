package io.golos.cyber_android.ui.screens.login_activity.di.on_boarding

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.onboardingImage.OnboardingUserImageFragment
import io.golos.cyber_android.ui.screens.login_sign_up_bio.OnboardingBioFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    OnBoardingFragmentModule::class,
    OnBoardingFragmentModuleBinds::class
])
@FragmentScope
interface OnBoardingFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: OnBoardingFragmentModule): Builder
        fun build(): OnBoardingFragmentComponent
    }

    fun inject(fragment: OnboardingBioFragment)
    fun inject(fragment: OnboardingUserImageFragment)
}