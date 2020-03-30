package io.golos.cyber_android.ui.screens.app_start.sign_up.activity.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.activity.SignUpActivity
import io.golos.cyber_android.ui.screens.app_start.sign_up.app_unlock.di.SignUpAppUnlockFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.confirm_password.di.SignUpConfirmPasswordFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.countries.di.SignUpCountryComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.di.SignUpCreatePasswordFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.di.SignUpPhoneFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.di.SignUpVerificationFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.pin.di.SignUpPinCodeFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.di.SignUpSelectMethodFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.di.SignUpNameFragmentComponent
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    SignUpActivityModuleChilds::class,
    SignUpActivityModuleBinds::class
])
@ActivityScope
interface SignUpActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SignUpActivityComponent
    }

    val signUpCreatePasswordFragmentComponent: SignUpCreatePasswordFragmentComponent.Builder
    val signUpConfirmPasswordFragmentComponent: SignUpConfirmPasswordFragmentComponent.Builder
    val signUpSelectMethodFragmentComponent: SignUpSelectMethodFragmentComponent.Builder
    val signUpCountryComponent: SignUpCountryComponent.Builder
    val signUpPhoneFragmentComponent: SignUpPhoneFragmentComponent.Builder
    val signUpVerificationFragmentComponent: SignUpVerificationFragmentComponent.Builder
    val signUpNameFragmentComponent: SignUpNameFragmentComponent.Builder
    val signUpPinCodeFragmentComponent: SignUpPinCodeFragmentComponent.Builder
    val signUpAppUnlockFragmentComponent: SignUpAppUnlockFragmentComponent.Builder

    fun inject(activity: SignUpActivity)
}