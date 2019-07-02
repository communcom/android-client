package io.golos.cyber_android.ui.screens.login.signin

import io.golos.cyber_android.ui.screens.login.signin.qrCode.detector.QrCodeDecrypted

interface SignInParentFragment {
    fun onQrCodeReceived(code: QrCodeDecrypted)
}