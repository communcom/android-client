package io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.view.SignUpPhoneVerificationFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpPhoneVerificationFragmentModuleBinds::class])
@FragmentScope
interface SignUpPhoneVerificationFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpPhoneVerificationFragmentComponent
    }

    fun inject(fragment: SignUpPhoneVerificationFragment)
}