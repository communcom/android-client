package io.golos.cyber_android.ui.screens.login_activity.di

import dagger.Module
import io.golos.cyber_android.ui.screens.login_activity.di.on_boarding.OnBoardingFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInUserNameFragmentComponent

@Module(subcomponents = [
    OnBoardingFragmentComponent::class,
    SignInUserNameFragmentComponent::class,
    SignInQrCodeFragmentComponent::class
])
class LoginActivityModuleChilds