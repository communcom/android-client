package io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.fingerprint_auth_fragment.FingerprintAuthFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.pin_code_auth_fragment.PinCodeAuthFragmentComponent

@Module(subcomponents = [
    FingerprintAuthFragmentComponent::class,
    PinCodeAuthFragmentComponent::class
])
class InAppAuthActivityModuleChilds {
}