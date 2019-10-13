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
import timber.log.Timber

class QrCodeDetector(private val appContext: Context) {

    /**
     * The interval after which we can show the error to the user again
     */
    var timeRepeatErrorMessageInMillis: Long = DEFAULT_TIME_REPEAT_ERROR_MESSAGE_IM_MILLIS

    private var lastShowErrorMessageTime: Long = 0

    private var onCodeReceivedListener: ((QrCodeDecrypted) -> Unit)? = null

    private var onDetectionErrorListener: ((QrCodeDetectorErrorCode) -> Unit)? = null

    private var camera: CameraSource? = null
    private var cameraView: SurfaceView? = null
    private var detector: BarcodeDetector? = null

    private val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private var cameraViewCallback: SurfaceHolder.Callback? = null

    private var isStartDetection = false

    fun setOnCodeReceivedListener(listener: ((QrCodeDecrypted) -> Unit)?) {
        onCodeReceivedListener = listener
    }

    fun setOnDetectionErrorListener(listener: ((QrCodeDetectorErrorCode) -> Unit)?) {
        onDetectionErrorListener = listener
    }

    fun startDetection(cameraView: SurfaceView) {
        if (isStartDetection) {
            return
        }

        isStartDetection = true

        // Create & check detector
        detector = BarcodeDetector.Builder(appContext).setBarcodeFormats(QR_CODE).build()

        if (!detector!!.isOperational) {
            onDetectionErrorListener?.invoke(QrCodeDetectorErrorCode.DETECTOR_IS_NOT_OPERATIONAL)
            return
        }

        // Setup camera
        try {
            camera = CameraSource.Builder(appContext, detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedFps(DEFAULT_CAMERA_FPS)
                .build()
        } catch (ex: Exception) {
            Timber.e(ex)
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
                stopDetection()
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

                for (i in 0 until items.size()) {
                    if (detector == null) {
                        break
                    }

                    tryToParseCode(items.valueAt(i).displayValue)
                }
            }
        })
    }

    fun stopDetection() {
        if (!isStartDetection) {
            return
        }

        cameraViewCallback?.let {
            cameraView?.holder?.removeCallback(it)
        }
        camera?.release()
        camera = null

        detector?.release()
        detector = null

        isStartDetection = false
    }

    private fun tryToParseCode(rawCode: String) {

        @Suppress("RegExpRedundantEscape")

        val keyParts = rawCode.split(" ")

        if (keyParts.size != 5) {

            val currentTimeMillis = System.currentTimeMillis()

            if(currentTimeMillis - lastShowErrorMessageTime >= timeRepeatErrorMessageInMillis){
                mainThreadHandler.post {
                    onDetectionErrorListener?.invoke(QrCodeDetectorErrorCode.INVALID_CODE)
                }
                lastShowErrorMessageTime = currentTimeMillis
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

    private companion object{

        /**
         * Default time for repeat error message in milli seconds. After this time, we can show error message for user again
         */
        private const val DEFAULT_TIME_REPEAT_ERROR_MESSAGE_IM_MILLIS = 3000L

        /**
         * Default value fps camera
         */
        private const val DEFAULT_CAMERA_FPS = 2.0f

    }
}