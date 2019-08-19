package io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.detector

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.Barcode.QR_CODE
import com.google.android.gms.vision.barcode.BarcodeDetector
import io.golos.cyber_android.application.App

class QrCodeDetector(private val appContext: Context) {
    private var onCodeReceivedListener: ((QrCodeDecrypted) -> Unit)? = null

    private var onDetectionErrorListener: ((QrCodeDetectorErrorCode) -> Unit)? = null

    private var camera: CameraSource? = null
    private var cameraView: SurfaceView? = null
    private var detector: BarcodeDetector? = null

    private val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private var cameraViewCallback: SurfaceHolder.Callback? = null

    fun setOnCodeReceivedListener(listener: ((QrCodeDecrypted) -> Unit)?) {
        onCodeReceivedListener = listener
    }

    fun setOnDetectionErrorListener(listener: ((QrCodeDetectorErrorCode) -> Unit)?) {
        onDetectionErrorListener = listener
    }

    fun startDetection(context: Context, cameraView: SurfaceView) {
        if(camera != null) {
            return
        }

        // Create & check detector
        detector = BarcodeDetector.Builder(appContext).setBarcodeFormats(QR_CODE).build()

        if(!detector!!.isOperational) {
            onDetectionErrorListener?.invoke(QrCodeDetectorErrorCode.DETECTOR_IS_NOT_OPERATIONAL)
            return
        }

        // Setup camera
        try {
            camera = CameraSource.Builder(context, detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()
        }
        catch(ex: Exception) {
            App.logger.log(ex)
            onDetectionErrorListener?.invoke(QrCodeDetectorErrorCode.DETECTOR_IS_NOT_OPERATIONAL)
            return
        }

        // Setup surface
        this.cameraView = cameraView

        cameraViewCallback = object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")      // Permission must be granted to this moment
            override fun surfaceCreated(holder: SurfaceHolder) {
                camera?.start(holder)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                // do nothing
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                camera?.stop()
            }
        }

        cameraView.holder.addCallback(cameraViewCallback)

        // Setup detector
        detector!!.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                // do nothing
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val items = detections.detectedItems

                for(i in 0 until items.size()) {
                    if(detector == null) {
                        break
                    }

                    tryToParseCode(items.valueAt(i).displayValue)
                }
            }
        })
    }

    fun stopDetection() {
        cameraViewCallback?.let {
            cameraView?.holder?.removeCallback(it)
        }

        camera?.stop()
        camera = null

        detector?.release()
        detector = null

        onDetectionErrorListener = null
        onCodeReceivedListener = null
    }

    private fun tryToParseCode(rawCode: String) {
        @Suppress("RegExpRedundantEscape")

        val keyParts = rawCode.split(" ")
        if (keyParts.size != 5) {
            mainThreadHandler.post {
                onDetectionErrorListener?.invoke(QrCodeDetectorErrorCode.INVALID_CODE)
            }
        } else {
            onCodeReceivedListener?.invoke(
                QrCodeDecrypted(
                    keyParts[0],        // User name
                    keyParts[1],        // Owner key
                    keyParts[2],        // Active key
                    keyParts[3],        // Posting key
                    keyParts[4])        // Memo key
            )
        }
    }
}