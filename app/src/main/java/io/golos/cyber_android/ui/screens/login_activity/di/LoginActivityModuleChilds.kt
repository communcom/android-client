package io.golos.cyber_android.ui.screens.login_activity.di

import dagger.Module
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInUserNameFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_confirm_password.di.SignUpConfirmPasswordFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_countries.di.SignUpCountryComponent
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.di.SignUpCreatePasswordFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.di.SignUpProtectionKeysFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_select_method.di.SignUpSelectMethodFragmentComponent

@Module(subcomponents = [
    SignInUserNameFragmentComponent::class,
    SignInQrCodeFragmentComponent::class,
    SignUpProtectionKeysFragmentComponent::class,
    SignUpCreatePasswordFragmentComponent::class,
    SignUpConfirmPasswordFragmentComponent::class,
    SignUpCountryComponent::class,
    SignUpSelectMethodFragmentComponent::class
])
class LoginActivityModuleChilds