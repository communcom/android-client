package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.view.SignUpProtectionKeysFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SignUpProtectionKeysFragmentModuleBinds::class])
@FragmentScope
interface SignUpProtectionKeysFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpProtectionKeysFragmentComponent
    }

    fun inject(fragment: SignUpProtectionKeysFragment)
}