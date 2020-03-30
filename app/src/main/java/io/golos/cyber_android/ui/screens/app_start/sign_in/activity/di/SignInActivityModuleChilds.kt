package io.golos.cyber_android.ui.screens.app_start.sign_in.activity.di

import dagger.Module
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.di.SignInAppUnlockFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.keys_backup.di.SignInProtectionKeysFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.di.SignInPinCodeFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.di.SignInUserNameFragmentComponent

@Module(subcomponents = [
    SignInAppUnlockFragmentComponent::class,
    SignInProtectionKeysFragmentComponent::class,
    SignInUserNameFragmentComponent::class,
    SignInQrCodeFragmentComponent::class,
    SignInPinCodeFragmentComponent::class
])
class SignInActivityModuleChilds