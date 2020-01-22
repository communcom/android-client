package io.golos.cyber_android.ui.screens.login_sign_up_countries.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_sign_up_countries.view.SignUpCountryFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpCountryModuleBinds::class])
@FragmentScope
interface SignUpCountryComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpCountryComponent
    }

    fun inject(fragment: SignUpCountryFragment)
}