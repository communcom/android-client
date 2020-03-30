package io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.view.SignInAppUnlockFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignInAppUnlockFragmentModuleBinds::class])
@FragmentScope
interface SignInAppUnlockFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignInAppUnlockFragmentComponent
    }

    fun inject(fragment: SignInAppUnlockFragment)
}