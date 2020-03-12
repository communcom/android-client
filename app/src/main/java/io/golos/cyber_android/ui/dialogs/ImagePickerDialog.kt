package io.golos.cyber_android.ui.dialogs

import android.Manifest
import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import kotlinx.android.synthetic.main.dialog_image_picker.*

/**
 * [BottomSheetDialogFragment] that provides possibility to pick the way to obtain some picture.
 * There are 3 possible outcomes - [RESULT_GALLERY] for gallery image, [RESULT_CAMERA] for camera image and [RESULT_DELETE]
 * for deleting current image.
 * This dialog checks all permission by itself. If calling site received one of the above result codes then app already has
 * all required permissions.
 */
class ImagePickerDialog : BottomSheetDialogFragmentBase<ImagePickerDialog.Result>() {
    sealed class Result {
        object Gallery: Result()
        object Camera: Result()
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSIONS = 4056
        private const val REQUEST_STORAGE_PERMISSIONS = 9071

        fun show(parent: Fragment, closeAction: (Result?) -> Unit) =
            ImagePickerDialog()
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "IMAGE_PICKER_DIALOG")
    }

    override val closeButton: View?
        get() = buttonClose

    override val layout: Int
        get() = R.layout.dialog_image_picker

    override fun setupView() {
        gallery.setOnClickListener {
            if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                closeOnItemSelected(Result.Gallery)
            } else {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSIONS)
            }
        }

        photo.setOnClickListener {
            if(checkPermission(Manifest.permission.CAMERA)) {
                closeOnItemSelected(Result.Gallery)
            } else {
                requestPermission(Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSIONS)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            REQUEST_CAMERA_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    closeOnItemSelected(Result.Camera)
                }
            }
            REQUEST_STORAGE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    closeOnItemSelected(Result.Gallery)
                }
            }
        }
    }

    private fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(permission: String, requestCode: Int) = requestPermissions(arrayOf(permission), requestCode)
}