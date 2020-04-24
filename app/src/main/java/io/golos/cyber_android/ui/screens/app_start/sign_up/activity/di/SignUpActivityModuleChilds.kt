package io.golos.cyber_android.ui.screens.app_start.sign_up.activity.di

import dagger.Module
import io.golos.cyber_android.ui.screens.app_start.sign_up.app_unlock.di.SignUpAppUnlockFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.confirm_password.di.SignUpConfirmPasswordFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.countries.di.SignUpCountryComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.di.SignUpCreatePasswordFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.email.di.SignUpEmailFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.di.SignUpEmailVerificationFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.di.SignUpPhoneFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.di.SignUpPhoneVerificationFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.pin.di.SignUpPinCodeFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.di.SignUpSelectMethodFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.di.SignUpNameFragmentComponent

@Module(subcomponents = [
    SignUpCreatePasswordFragmentComponent::class,
    SignUpConfirmPasswordFragmentComponent::class,
    SignUpCountryComponent::class,
    SignUpSelectMethodFragmentComponent::class,
    SignUpPhoneFragmentComponent::class,
    SignUpEmailFragmentComponent::class,
    SignUpPhoneVerificationFragmentComponent::class,
    SignUpEmailVerificationFragmentComponent::class,
    SignUpPinCodeFragmentComponent::class,
    SignUpAppUnlockFragmentComponent::class,
    SignUpNameFragmentComponent::class
])
class SignUpActivityModuleChilds