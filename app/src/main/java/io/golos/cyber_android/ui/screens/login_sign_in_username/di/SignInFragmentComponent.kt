package io.golos.cyber_android.ui.screens.login_sign_in_username.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.view.SignInFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignInFragmentModuleBinds::class])
@FragmentScope
interface SignInFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignInFragmentComponent
    }

    fun inject(fragment: SignInFragment)
}