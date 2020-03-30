package io.golos.cyber_android.ui.screens.app_start.sign_in.shared.data_pass

import io.golos.cyber_android.ui.dto.QrCodeContent

interface SignInFragmentsDataPass {
    fun putQrCode(qrCode: QrCodeContent)
    fun getQrCode(): QrCodeContent?
}