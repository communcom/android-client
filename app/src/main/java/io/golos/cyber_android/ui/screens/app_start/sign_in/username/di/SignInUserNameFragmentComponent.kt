package io.golos.cyber_android.ui.screens.app_start.sign_in.username.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.view.SignInUserNameFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignInUserNameFragmentModuleBinds::class])
@FragmentScope
interface SignInUserNameFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignInUserNameFragmentComponent
    }

    fun inject(fragment: SignInUserNameFragment)
}