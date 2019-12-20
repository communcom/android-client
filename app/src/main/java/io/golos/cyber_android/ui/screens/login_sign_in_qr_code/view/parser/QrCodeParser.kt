package io.golos.cyber_android.ui.screens.login_sign_in_qr_code.view.parser

import io.golos.cyber_android.ui.dto.QrCodeContent

interface QrCodeParser {
    /**
     * @return null if can't parse
     */
    fun parse(rawText: String): QrCodeContent?
}