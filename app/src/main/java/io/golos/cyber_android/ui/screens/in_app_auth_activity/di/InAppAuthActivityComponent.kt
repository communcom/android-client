package io.golos.cyber_android.ui.screens.in_app_auth_activity.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.di.FingerprintAuthFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.di.PinCodeAuthFragmentComponent
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