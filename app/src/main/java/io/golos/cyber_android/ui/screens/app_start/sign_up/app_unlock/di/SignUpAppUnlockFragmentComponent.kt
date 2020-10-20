package io.golos.cyber_android.ui.screens.app_start.sign_up.app_unlock.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.app_unlock.view.SignUpAppUnlockFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpAppUnlockFragmentModuleBinds::class])
@FragmentScope
interface SignUpAppUnlockFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpAppUnlockFragmentComponent
    }

    fun inject(fragment: SignUpAppUnlockFragment)

}