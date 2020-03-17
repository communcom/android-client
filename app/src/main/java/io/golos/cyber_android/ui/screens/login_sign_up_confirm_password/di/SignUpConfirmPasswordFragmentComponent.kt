package io.golos.cyber_android.ui.screens.login_sign_up_confirm_password.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_sign_up_confirm_password.view.SignUpConfirmPasswordFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpConfirmPasswordFragmentModuleBinds::class])
@FragmentScope
interface SignUpConfirmPasswordFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpConfirmPasswordFragmentComponent
    }

    fun inject(fragment: SignUpConfirmPasswordFragment)
}