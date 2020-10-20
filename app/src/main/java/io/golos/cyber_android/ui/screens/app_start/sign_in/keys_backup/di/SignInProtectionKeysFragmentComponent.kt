package io.golos.cyber_android.ui.screens.app_start.sign_in.keys_backup.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.keys_backup.view.SignUpProtectionKeysFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignInProtectionKeysFragmentModuleBinds::class])
@FragmentScope
interface SignInProtectionKeysFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignInProtectionKeysFragmentComponent
    }

    fun inject(fragment: SignUpProtectionKeysFragment)
}