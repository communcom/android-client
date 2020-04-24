package io.golos.cyber_android.ui.screens.app_start.sign_up.email.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.email.view.SignUpEmailFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpEmailFragmentModuleBinds::class])
@FragmentScope
interface SignUpEmailFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpEmailFragmentComponent
    }

    fun inject(fragment: SignUpEmailFragment)
}