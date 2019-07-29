package io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.detector

enum class QrCodeDetectorErrorCode {
    /**
     * Something wrong with a detector
     */
    DETECTOR_IS_NOT_OPERATIONAL,

    /**
     * QR-code can't be decrypted
     */
    INVALID_CODE
}