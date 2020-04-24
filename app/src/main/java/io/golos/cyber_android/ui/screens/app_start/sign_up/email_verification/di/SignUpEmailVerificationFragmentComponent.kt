package io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.view.SignUpEmailVerificationFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpEmailVerificationFragmentModuleBinds::class])
@FragmentScope
interface SignUpEmailVerificationFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpEmailVerificationFragmentComponent
    }

    fun inject(fragment: SignUpEmailVerificationFragment)
}