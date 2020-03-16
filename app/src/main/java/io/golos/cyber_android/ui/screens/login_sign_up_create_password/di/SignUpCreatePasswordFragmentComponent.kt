package io.golos.cyber_android.ui.screens.login_sign_up_create_password.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.view.SignUpCreatePasswordFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpCreatePasswordFragmentModuleBinds::class])
@FragmentScope
interface SignUpCreatePasswordFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpCreatePasswordFragmentComponent
    }

    fun inject(fragment: SignUpCreatePasswordFragment)
}