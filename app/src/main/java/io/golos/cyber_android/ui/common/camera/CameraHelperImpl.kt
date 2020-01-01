package io.golos.cyber_android.ui.common.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import io.golos.cyber_android.BuildConfig
import io.golos.domain.utils.IdUtil
import java.io.File
import javax.inject.Inject

class CameraHelperImpl
@Inject
constructor(
    private val appContext: Context
) : CameraHelper {
    companion object {
        private const val REQUEST_IMAGE_CAMERA_CAPTURE = 200
    }

    private lateinit var currentImageFile: Uri

    override fun takeCameraPhoto(fragment: Fragment) {
        currentImageFile = getCacheImagePath(IdUtil.generateLongId().toString() + ".jpg")
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageFile)

        if (takePictureIntent.resolveActivity(appContext.packageManager) != null) {
            fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAMERA_CAPTURE)
        }
    }

    override fun processCameraPhotoResult(requestCode: Int, resultCode: Int, successAction: (Uri) -> Unit) {
        if(resultCode != Activity.RESULT_OK || requestCode != REQUEST_IMAGE_CAMERA_CAPTURE) {
            return
        }

        successAction(currentImageFile)
    }

    private fun getCacheImagePath(fileName: String): Uri {
        val file = File.createTempFile("JPG_", fileName, appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        return FileProvider.getUriForFile(appContext, BuildConfig.APPLICATION_ID + ".fileprovider", file)
    }
}