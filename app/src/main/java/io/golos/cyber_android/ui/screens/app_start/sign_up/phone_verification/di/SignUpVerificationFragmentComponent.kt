package io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.view.SignUpVerificationFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpVerificationFragmentModuleBinds::class])
@FragmentScope
interface SignUpVerificationFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpVerificationFragmentComponent
    }

    fun inject(fragment: SignUpVerificationFragment)
}