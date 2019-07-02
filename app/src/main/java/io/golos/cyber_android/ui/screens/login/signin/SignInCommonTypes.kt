package io.golos.cyber_android.ui.screens.login.signin

import androidx.annotation.StringRes
import io.golos.cyber_android.R

enum class SignInTab(@StringRes val title: Int, val index: Int) {
    SCAN_QR(R.string.sign_in_scan_qr, 0),
    LOGIN_KEY(R.string.sign_in_login_and_key, 1);

    companion object {
        fun fromIndex(index: Int) = values().first { it.index == index }
    }
}

object SignInRequestCodes {
    const val SIGN_IN_SCAN_QR_REQUEST_CODE = 100
    const val SIGN_IN_LOGIN_KEY_REQUEST_CODE = 101
}

object SignInArgs {
    const val TAB_CODE = "TAB_CODE"
}
