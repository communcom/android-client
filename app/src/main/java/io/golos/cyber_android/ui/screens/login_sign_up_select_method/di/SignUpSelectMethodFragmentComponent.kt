package io.golos.cyber_android.ui.screens.login_sign_up_select_method.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.SignUpSelectMethodFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpSelectMethodFragmentModuleBinds::class])
@FragmentScope
interface SignUpSelectMethodFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpSelectMethodFragmentComponent
    }

    fun inject(fragment: SignUpSelectMethodFragment)
}