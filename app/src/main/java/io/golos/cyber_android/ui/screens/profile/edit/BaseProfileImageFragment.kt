package io.golos.cyber_android.ui.screens.profile.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.screens.profile.edit.cover.EditProfileCoverActivity
import java.io.File

private const val REQUEST_IMAGE_CAPTURE = 200
private const val REQUEST_GALLERY_IMAGE = 201

abstract class BaseProfileImageFragment : LoadingFragment() {

    private var currentImageFileName = ""

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        clearCache(requireContext())

        if (savedInstanceState == null) {
            if (getImageSource() == EditProfileCoverActivity.ImageSource.CAMERA) {
                takeCameraPhoto()
            } else {
                pickGalleryPhoto()
            }
        }
    }

    private fun getImageSource() = (arguments?.getSerializable(Tags.ARGS) as? EditProfileCoverActivity.ImageSource
        ?: EditProfileCoverActivity.ImageSource.CAMERA)

    private fun pickGalleryPhoto() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> if (resultCode == Activity.RESULT_OK) {
                onImagePicked(getCacheImagePath(currentImageFileName))
            } else {
                onCancel()
            }
            REQUEST_GALLERY_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                data?.data?.let {
                    val path = File(requireActivity().externalCacheDir, "camera")
                    if (!path.exists()) path.mkdirs()
                    val imageFile = File(path, System.currentTimeMillis().toString() + ".jpg")
                    requireContext().contentResolver.openInputStream(it).use { input ->
                        imageFile
                            .outputStream()
                            .use { fileOut ->
                                input?.copyTo(fileOut)
                            }
                    }

                    onImagePicked(Uri.fromFile(imageFile))
                }
            } else {
                onCancel()
            }
            else -> onCancel()
        }
    }

    abstract fun onImagePicked(uri: Uri)

    private fun onCancel() {
        requireActivity().setResult(Activity.RESULT_CANCELED)
        requireActivity().finish()
    }

    private fun takeCameraPhoto() {
        currentImageFileName = System.currentTimeMillis().toString() + ".jpg"
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(currentImageFileName))
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun getCacheImagePath(fileName: String): Uri {
        val path = File(requireActivity().externalCacheDir, "camera")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)

        return if (android.os.Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", image)
        } else {
            Uri.fromFile(image)
        }
    }

    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
    private fun clearCache(context: Context) {
        val path = File(context.externalCacheDir, "camera")
        if (path.exists() && path.isDirectory) {
            for (child in path.listFiles()) {
                child.delete()
            }
        }
    }
}
