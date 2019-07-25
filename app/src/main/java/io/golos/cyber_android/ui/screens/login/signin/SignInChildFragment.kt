package io.golos.cyber_android.ui.screens.login.signin

interface SignInChildFragment {
    val tabCode: SignInTab

    fun onSelected() {}

    fun onUnselected() {}
}