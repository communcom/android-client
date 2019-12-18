package io.golos.cyber_android.ui.screens.login_activity.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.login_activity.di.on_boarding.OnBoardingFragmentComponent
import io.golos.cyber_android.ui.screens.login_activity.LoginActivity
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_old.qr_code.QrCodeSignInFragment
import io.golos.cyber_android.ui.screens.login_sign_in_old.user_name.UserNameSignInFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.country.SignUpCountryFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint.FingerprintFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.key.SignUpKeyFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.keys_backup.SignUpProtectionKeysFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.name.SignUpNameFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.phone.SignUpPhoneFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.pin.PinCodeFragment
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.verification.SignUpVerificationFragment
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

    val onBoardingFragmentComponent: OnBoardingFragmentComponent.Builder
    val signInFragmentComponent: SignInFragmentComponent.Builder

    fun inject(activity: LoginActivity)
    fun inject(fragment: QrCodeSignInFragment)
    fun inject(fragment: UserNameSignInFragment)
    fun inject(fragment: SignUpNameFragment)
    fun inject(fragment: SignUpPhoneFragment)
    fun inject(fragment: SignUpVerificationFragment)
    fun inject(fragment: SignUpCountryFragment)
    fun inject(fragment: FingerprintFragment)
    fun inject(fragment: SignUpKeyFragment)
    fun inject(fragment: SignUpProtectionKeysFragment)
    fun inject(fragment: PinCodeFragment)
}