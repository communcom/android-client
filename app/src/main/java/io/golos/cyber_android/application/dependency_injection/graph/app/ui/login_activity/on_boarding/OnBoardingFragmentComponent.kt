package io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.on_boarding

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.onboardingImage.OnboardingUserImageFragment
import io.golos.cyber_android.ui.shared_fragments.bio.OnboardingBioFragment
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