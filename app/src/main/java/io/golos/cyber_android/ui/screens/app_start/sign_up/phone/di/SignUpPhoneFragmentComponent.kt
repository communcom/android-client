package io.golos.cyber_android.ui.screens.app_start.sign_up.phone.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.view.SignUpPhoneFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpPhoneFragmentModuleBinds::class])
@FragmentScope
interface SignUpPhoneFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpPhoneFragmentComponent
    }

    fun inject(fragment: SignUpPhoneFragment)
}