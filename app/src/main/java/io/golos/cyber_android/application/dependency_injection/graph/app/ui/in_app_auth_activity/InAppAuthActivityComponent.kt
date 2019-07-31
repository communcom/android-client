package io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity

import dagger.Subcomponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.fingerprint_auth_fragment.FingerprintAuthFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.pin_code_auth_fragment.PinCodeAuthFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.InAppAuthActivity
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    InAppAuthActivityModuleBinds::class,
    InAppAuthActivityModuleChilds::class,
    InAppAuthActivityModule::class
])
@ActivityScope
interface InAppAuthActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): InAppAuthActivityComponent
    }

    fun inject(activity: InAppAuthActivity)

    val fingerprintAuthFragmentComponent: FingerprintAuthFragmentComponent.Builder
    val pinCodeAuthFragmentComponent: PinCodeAuthFragmentComponent.Builder
}