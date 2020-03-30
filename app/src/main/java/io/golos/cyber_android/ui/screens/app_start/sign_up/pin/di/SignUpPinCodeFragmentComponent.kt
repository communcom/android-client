package io.golos.cyber_android.ui.screens.app_start.sign_up.pin.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.pin.view.SignUpPinCodeFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpPinCodeFragmentModuleBinds::class])
@FragmentScope
interface SignUpPinCodeFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpPinCodeFragmentComponent
    }

    fun inject(fragment: SignUpPinCodeFragment)
}