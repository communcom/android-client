package io.golos.cyber_android.ui.screens.in_app_auth_activity.di

import dagger.Module
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.di.FingerprintAuthFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.di.PinCodeAuthFragmentComponent

@Module(subcomponents = [
    FingerprintAuthFragmentComponent::class,
    PinCodeAuthFragmentComponent::class
])
class InAppAuthActivityModuleChilds