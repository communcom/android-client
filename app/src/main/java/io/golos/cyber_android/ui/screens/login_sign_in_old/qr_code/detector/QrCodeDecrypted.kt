package io.golos.cyber_android.ui.screens.login_sign_in_old.qr_code.detector

data class QrCodeDecrypted (
    val userName: String,

    val ownerKey: String,
    val activeKey: String,
    val postingKey: String,
    val memoKey: String
)