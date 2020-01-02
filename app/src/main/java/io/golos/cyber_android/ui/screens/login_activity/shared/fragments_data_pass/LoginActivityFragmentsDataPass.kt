package io.golos.cyber_android.ui.screens.login_activity.shared.fragments_data_pass

import io.golos.cyber_android.ui.dto.QrCodeContent

interface LoginActivityFragmentsDataPass {
    fun putQrCode(qrCode: QrCodeContent)

    fun getQrCode(): QrCodeContent?
}