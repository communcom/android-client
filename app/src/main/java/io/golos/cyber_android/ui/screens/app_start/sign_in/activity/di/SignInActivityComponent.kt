package io.golos.cyber_android.ui.screens.app_start.sign_in.activity.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.activity.SignInActivity
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.di.SignInAppUnlockFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.keys_backup.di.SignInProtectionKeysFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.di.SignInPinCodeFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.di.SignInUserNameFragmentComponent
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    SignInActivityModuleChilds::class,
    SignInActivityModuleBinds::class
])
@ActivityScope
interface SignInActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignInActivityComponent
    }

    val signInUserNameFragmentComponent: SignInUserNameFragmentComponent.Builder
    val signInQrCodeFragmentComponent: SignInQrCodeFragmentComponent.Builder
    val signInProtectionKeysFragmentComponent: SignInProtectionKeysFragmentComponent.Builder
    val signInPinCodeFragmentComponent: SignInPinCodeFragmentComponent.Builder
    val signInAppUnlockFragmentComponent: SignInAppUnlockFragmentComponent.Builder

    fun inject(activity: SignInActivity)
}