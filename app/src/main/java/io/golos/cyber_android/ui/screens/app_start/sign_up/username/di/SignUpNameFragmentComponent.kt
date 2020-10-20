package io.golos.cyber_android.ui.screens.app_start.sign_up.username.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.view.SignUpNameFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpNameFragmentModuleBinds::class])
@FragmentScope
interface SignUpNameFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpNameFragmentComponent
    }

    fun inject(fragment: SignUpNameFragment)
}