package io.golos.cyber_android.ui.screens.login_sign_up_confirm_password.view

import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.login_sign_up_confirm_password.di.SignUpConfirmPasswordFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.view.SignUpCreatePasswordFragment

class SignUpConfirmPasswordFragment() : SignUpCreatePasswordFragment() {

    override fun inject(key: String) = App.injections.get<SignUpConfirmPasswordFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpConfirmPasswordFragmentComponent>(key)
}