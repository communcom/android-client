package io.golos.cyber_android.ui.screens.login_activity.di

import dagger.Module
import io.golos.cyber_android.ui.screens.login_activity.di.on_boarding.OnBoardingFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInUserNameFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_countries.di.SignUpCountryComponent
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.di.SignUpProtectionKeysFragmentComponent

@Module(subcomponents = [
    OnBoardingFragmentComponent::class,
    SignInUserNameFragmentComponent::class,
    SignInQrCodeFragmentComponent::class,
    SignUpProtectionKeysFragmentComponent::class,
    SignUpCountryComponent::class
])
class LoginActivityModuleChilds