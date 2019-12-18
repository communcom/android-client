package io.golos.cyber_android.ui.screens.login_activity.di

import dagger.Module
import io.golos.cyber_android.ui.screens.login_activity.di.on_boarding.OnBoardingFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_username.di.SignInFragmentComponent

@Module(subcomponents = [
    OnBoardingFragmentComponent::class,
    SignInFragmentComponent::class
])
class LoginActivityModuleChilds