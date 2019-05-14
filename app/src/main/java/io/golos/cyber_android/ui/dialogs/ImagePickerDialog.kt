package io.golos.cyber_android.ui.dialogs

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.dialog_image_picker.*

const val REQUEST_CAMERA_PERMISSIONS = 200

class ImagePickerDialog : BottomSheetDialogFragment() {

    enum class Target(@StringRes val removeMsg: Int) {
        COVER(R.string.delete_current_cover), AVATAR(R.string.delete_current_photo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_image_picker,
            container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val target = arguments?.getSerializable("target") as Target
        remove.setText(target.removeMsg)

        fromGallery.setOnClickListener {
            targetFragment?.onActivityResult(targetRequestCode, RESULT_GALLERY, null)
            dismiss()
        }

        fromCamera.setOnClickListener {
            if (!checkCameraPermissions()) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSIONS
                )
            } else {
                targetFragment?.onActivityResult(targetRequestCode, RESULT_CAMERA, null)
                dismiss()
            }
        }

        remove.setOnClickListener {
            targetFragment?.onActivityResult(targetRequestCode, RESULT_DELETE, null)
            dismiss()
        }

    }

    private fun checkCameraPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSIONS) {
            if ((grantResults.size > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                targetFragment?.onActivityResult(targetRequestCode, RESULT_CAMERA, null)
                dismiss()
            }
        }
    }

    companion object {
        const val RESULT_GALLERY = Activity.RESULT_FIRST_USER + 1
        const val RESULT_CAMERA = Activity.RESULT_FIRST_USER + 2
        const val RESULT_DELETE = Activity.RESULT_FIRST_USER + 3

        fun newInstance(target: Target): ImagePickerDialog {
            return ImagePickerDialog().apply {
                arguments = Bundle().apply {
                    putSerializable("target", target)
                }
            }
        }
    }
}