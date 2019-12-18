package io.golos.cyber_android.ui.screens.login_sign_in_old

import androidx.annotation.StringRes
import io.golos.cyber_android.R

enum class SignInTab(@StringRes val title: Int, val index: Int) {
    SCAN_QR(R.string.sign_in_scan_qr, 0),
    LOGIN_KEY(R.string.sign_in_login_and_key, 1);

    companion object {
        fun fromIndex(index: Int) = values().first { it.index == index }
    }
}

object SignInArgs {
    const val TAB_CODE = "TAB_CODE"
}
