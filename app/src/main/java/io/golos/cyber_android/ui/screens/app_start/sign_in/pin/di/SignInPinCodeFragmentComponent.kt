package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.view.SignInPinCodeFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignInPinCodeFragmentModuleBinds::class])
@FragmentScope
interface SignInPinCodeFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignInPinCodeFragmentComponent
    }

    fun inject(fragment: SignInPinCodeFragment)
}