package io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity

import dagger.Subcomponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.on_boarding.OnBoardingFragmentComponent
import io.golos.cyber_android.ui.screens.login_activity.LoginActivity
import io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.QrCodeSignInFragment
import io.golos.cyber_android.ui.screens.login_activity.signin.user_name.UserNameSignInFragment
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country.SignUpCountryFragment
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.fingerprint.FingerprintFragment
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.key.SignUpKeyFragment
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.keys_backup.SignUpProtectionKeysFragment
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.name.SignUpNameFragment
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.phone.SignUpPhoneFragment
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin.PinCodeFragment
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.verification.SignUpVerificationFragment
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