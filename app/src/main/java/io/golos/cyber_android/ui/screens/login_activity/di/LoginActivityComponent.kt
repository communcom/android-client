package io.golos.cyber_android.ui.screens.login_activity.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_activity.view.LoginActivity
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInUserNameFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint.FingerprintFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.name.SignUpNameFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.phone.SignUpPhoneFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.verification.SignUpVerificationFragment
import io.golos.cyber_android.ui.screens.login_sign_up_countries.di.SignUpCountryComponent
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.di.SignUpProtectionKeysFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_pin.PinCodeFragment
import io.golos.cyber_android.ui.screens.login_welcome.WelcomeFragment
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    LoginActivityModuleChilds::class,
    LoginActivityModuleBinds::class
])
@ActivityScope
interface LoginActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): LoginActivityComponent
    }

    val signInUserNameFragmentComponent: SignInUserNameFragmentComponent.Builder
    val signInQrCodeFragmentComponent: SignInQrCodeFragmentComponent.Builder
    val signUpProtectionKeysFragmentComponent: SignUpProtectionKeysFragmentComponent.Builder
    val signUpCountryComponent: SignUpCountryComponent.Builder

    fun inject(activity: LoginActivity)
    fun inject(fragment: SignUpNameFragment)
    fun inject(fragment: SignUpPhoneFragment)
    fun inject(fragment: SignUpVerificationFragment)
    fun inject(fragment: FingerprintFragment)
    fun inject(fragment: PinCodeFragment)
    fun inject(fragment: WelcomeFragment)
}