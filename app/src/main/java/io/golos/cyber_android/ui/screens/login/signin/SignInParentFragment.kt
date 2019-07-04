package io.golos.cyber_android.ui.screens.login.signin

import io.golos.cyber_android.ui.screens.login.signin.qr_code.detector.QrCodeDecrypted

interface SignInParentFragment {
    fun onQrCodeReceived(code: QrCodeDecrypted)
}