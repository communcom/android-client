package io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view.detector

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import dagger.Lazy
import io.golos.cyber_android.ui.dto.QrCodeContent
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.dto.QrCodeDetectorErrorCode
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view.parser.QrCodeParser
import timber.log.Timber
import javax.inject.Inject

class QrCodeDetectorImpl
@Inject
constructor(
    private val parser: Lazy<QrCodeParser>
) : QrCodeDetector {
    private companion object{
        // Default time for repeat error message in milli seconds. After this time, we can show error message for user again
        private const val DEFAULT_TIME_REPEAT_ERROR_MESSAGE_IM_MILLIS = 3000L

        // Default value fps camera
        private const val DEFAULT_CAMERA_FPS = 2.0f
    }

    // The interval after which we can show the error to the user again
    private var timeRepeatErrorInMillis: Long = DEFAULT_TIME_REPEAT_ERROR_MESSAGE_IM_MILLIS

    private var lastErrorTime: Long = 0

    private var onCodeReceivedListener: ((QrCodeContent) -> Unit)? = null

    private var onDetectionErrorListener: ((QrCodeDetectorErrorCode) -> Unit)? = null

    private var camera: CameraSource? = null
    private var cameraView: SurfaceView? = null
    private var detector: BarcodeDetector? = null

    private val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private var cameraViewCallback: SurfaceHolder.Callback? = null

    private var isStartDetection = false

    override fun setOnCodeReceivedListener(listener: ((QrCodeContent) -> Unit)?) {
        onCodeReceivedListener = listener
    }

    override fun setOnDetectionErrorListener(listener: ((QrCodeDetectorErrorCode) -> Unit)?) {
        onDetectionErrorListener = listener
    }

    override fun startDetection(cameraView: SurfaceView) {
        if (isStartDetection) {
            return
        }

        isStartDetection = true

        val context = cameraView.context

        // Create & check detector
        detector = BarcodeDetector
            .Builder(context)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()

        if (!detector!!.isOperational) {
            onDetectionErrorListener?.invoke(QrCodeDetectorErrorCode.DETECTOR_IS_NOT_OPERATIONAL)
            return
        }

        // Setup camera
        try {
            camera = CameraSource
                .Builder(context, detector)
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

        // Setup camera callback
        cameraViewCallback = object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")      // Permission must be granted to this moment
            override fun surfaceCreated(holder: SurfaceHolder) {
                camera!!.start(holder)
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

                // Try to process receiver code
                for (i in 0 until items.size()) {
                    val code = parser.get().parse(items.valueAt(i).displayValue)

                    if(code != null) {
                        mainThreadHandler.post { onCodeReceivedListener?.invoke(code) }
                        break
                    } else {
                        val currentTimeMillis = System.currentTimeMillis()

                        if(currentTimeMillis - lastErrorTime >= timeRepeatErrorInMillis){
                            mainThreadHandler.post { onDetectionErrorListener?.invoke(QrCodeDetectorErrorCode.INVALID_CODE) }
                            lastErrorTime = currentTimeMillis
                        }
                    }
                }
            }
        })
    }

    override fun stopDetection() {
        if (!isStartDetection) {
            return
        }

        cameraViewCallback?.let {
            cameraView?.holder?.removeCallback(it)
        }
        cameraView = null

        camera?.release()
        camera = null

        detector?.release()
        detector = null

        isStartDetection = false
    }
}