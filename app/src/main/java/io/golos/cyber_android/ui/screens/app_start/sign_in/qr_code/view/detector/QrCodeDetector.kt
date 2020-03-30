package io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view.detector

import android.view.SurfaceView
import io.golos.cyber_android.ui.dto.QrCodeContent
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.dto.QrCodeDetectorErrorCode

interface QrCodeDetector {
    fun setOnCodeReceivedListener(listener: ((QrCodeContent) -> Unit)?)

    fun setOnDetectionErrorListener(listener: ((QrCodeDetectorErrorCode) -> Unit)?)

    fun startDetection(cameraView: SurfaceView)

    fun stopDetection()
}