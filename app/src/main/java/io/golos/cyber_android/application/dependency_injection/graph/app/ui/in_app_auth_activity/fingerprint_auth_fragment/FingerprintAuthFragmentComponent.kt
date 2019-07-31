package io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.fingerprint_auth_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.FingerprintAuthFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    FingerprintAuthFragmentModule::class,
    FingerprintAuthFragmentModuleBinds::class
])
@FragmentScope
interface FingerprintAuthFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: FingerprintAuthFragmentModule): Builder
        fun build(): FingerprintAuthFragmentComponent
    }

    fun inject(fragment: FingerprintAuthFragment)
}