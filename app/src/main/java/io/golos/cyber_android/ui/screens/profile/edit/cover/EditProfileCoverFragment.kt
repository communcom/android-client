package io.golos.cyber_android.ui.screens.profile.edit.cover

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.LoadingFragment
import kotlinx.android.synthetic.main.edit_profile_cover_fragment.*
import java.io.File


private const val REQUEST_IMAGE_CAPTURE = 200
private const val REQUEST_GALLERY_IMAGE = 201

class EditProfileCoverFragment : LoadingFragment() {

    private lateinit var viewModel: EditProfileCoverViewModel

    private var currentImageFileName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_profile_cover_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        clearCache(requireContext())

        if (savedInstanceState == null) {
            val source = arguments?.getSerializable(Tags.ARGS) as? EditProfileCoverActivity.ImageSource
                ?: EditProfileCoverActivity.ImageSource.CAMERA

            if (source == EditProfileCoverActivity.ImageSource.CAMERA) {
                takeCameraPhoto()
            } else {
                pickGalleryPhoto()
            }
        }
    }

    private fun pickGalleryPhoto() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
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
                    onImagePicked(it)
                }
            } else {
                onCancel()
            }
            else -> onCancel()
        }
    }

    private fun onImagePicked(uri: Uri) {
        Glide.with(requireContext())
            .load(uri)
            .into(cover)
    }

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

    private fun queryName(resolver: ContentResolver, uri: Uri): String {
        val returnCursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
    fun clearCache(context: Context) {
        val path = File(context.externalCacheDir, "camera")
        if (path.exists() && path.isDirectory) {
            for (child in path.listFiles()) {
                child.delete()
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(EditProfileCoverViewModel::class.java)
    }

    companion object {
        fun newInstance(source: EditProfileCoverActivity.ImageSource) =
            EditProfileCoverFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(Tags.ARGS, source)
                }
            }
    }


}
