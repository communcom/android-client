package io.golos.cyber_android.ui.screens.login.signin

import io.golos.cyber_android.ui.screens.login.signin.qrCode.detector.QrCodeDecrypted

interface SignInChildFragment {
    val tabCode: SignInTab

    fun onSelected() {}

    fun onUnselected() {}

    fun onQrCodeReceived(code: QrCodeDecrypted) {}
}