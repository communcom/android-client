package io.golos.cyber_android.ui.screens.login_sign_in_old

interface SignInChildFragment {
    val tabCode: SignInTab

    fun onSelected() {}

    fun onUnselected() {}
}