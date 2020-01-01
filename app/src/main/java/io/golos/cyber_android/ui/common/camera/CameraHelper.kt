package io.golos.cyber_android.ui.common.camera

import android.net.Uri
import androidx.fragment.app.Fragment

interface CameraHelper {
    fun takeCameraPhoto(fragment: Fragment)

    fun processCameraPhotoResult(requestCode: Int, resultCode: Int, successAction: (Uri) -> Unit)
}