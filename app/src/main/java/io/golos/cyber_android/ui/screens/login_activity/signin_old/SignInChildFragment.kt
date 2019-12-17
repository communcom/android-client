package io.golos.cyber_android.ui.screens.login_activity.signin_old

interface SignInChildFragment {
    val tabCode: SignInTab

    fun onSelected() {}

    fun onUnselected() {}
}