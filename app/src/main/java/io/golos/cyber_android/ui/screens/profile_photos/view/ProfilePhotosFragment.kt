package io.golos.cyber_android.ui.screens.profile_photos.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentProfilePhotosBinding
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.screens.profile_photos.di.ProfilePhotosFragmentComponent
import io.golos.cyber_android.ui.screens.profile_photos.dto.InitPhotoPreviewCommand
import io.golos.cyber_android.ui.screens.profile_photos.dto.PassResultCommand
import io.golos.cyber_android.ui.screens.profile_photos.dto.RequestResultImageCommand
import io.golos.cyber_android.ui.screens.profile_photos.dto.StartCameraCommand
import io.golos.cyber_android.ui.screens.profile_photos.view.grid.GalleryGridAdapter
import io.golos.cyber_android.ui.screens.profile_photos.view_model.ProfilePhotosViewModel
import io.golos.cyber_android.ui.shared.camera.CameraHelper
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_profile_photos.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.File
import javax.inject.Inject

@RuntimePermissions
class ProfilePhotosFragment : FragmentBaseMVVM<FragmentProfilePhotosBinding, ProfilePhotosViewModel>() {
    @Parcelize
    data class Result(
        val photoFilePath: String,
        val place: ProfileItem
    ): Parcelable

    companion object {
        private const val ITEM = "ITEM"
        private const val IMAGE_URL = "IMAGE_URL"

        const val RESULT = "RESULT"
        const val REQUEST = 1657

        fun newInstance(item: ProfileItem, imageUrl: String?, parentFragment: Fragment): ProfilePhotosFragment {
            return ProfilePhotosFragment().apply {
                arguments = Bundle().apply {
                    putInt(ITEM, item.value)
                    putString(IMAGE_URL, imageUrl)
                }
                setTargetFragment(parentFragment, REQUEST)
            }
        }
    }

    private lateinit var galleryGridAdapter: GalleryGridAdapter
    private lateinit var galleryGridLayoutManager: GridLayoutManager

    @Inject
    internal lateinit var cameraHelper: CameraHelper

    override fun provideViewModelType(): Class<ProfilePhotosViewModel> = ProfilePhotosViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_photos

    override fun inject(key: String) =
        App.injections
            .get<ProfilePhotosFragmentComponent>(
                key,
                ProfileItem.create(arguments!!.getInt(ITEM)),
                arguments!!.getString(IMAGE_URL))
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<ProfilePhotosFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentProfilePhotosBinding, viewModel: ProfilePhotosViewModel) {
        binding.viewModel = viewModel
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
            is InitPhotoPreviewCommand -> initPhotoPreview(command.profileItem, command.imageUrl, command.isImageFromCamera)
            is StartCameraCommand -> startCameraWithPermissionCheck()
            is RequestResultImageCommand -> requestResultImage()
            is PassResultCommand -> passResult(command.imageFile, command.profileItem)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewImage.setOnLoadingCompleteListener { viewModel.onPreviewLoadingCompleted() }

        viewModel.galleryItems.observe({viewLifecycleOwner.lifecycle}) { updateGallery(it) }

        startWithPermissionCheck()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        cameraHelper.processCameraPhotoResult(requestCode, resultCode) { imageUri -> viewModel.onCameraImageCaptured(imageUri) }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun start() = viewModel.start()

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun onStoragePermissionsDenied() = viewModel.onStoragePermissionDenied()

    @NeedsPermission(Manifest.permission.CAMERA)
    internal fun startCamera() = cameraHelper.takeCameraPhoto(this)

    @OnPermissionDenied(Manifest.permission.CAMERA)
    internal fun onCameraPermissionsDenied() { /*do nothing*/ }

    private fun initPhotoPreview(place: ProfileItem, imageUrl: String?, isImageFromCamera: Boolean) {
        previewImage.setMode(place)
        previewImage.load(imageUrl, isImageFromCamera)
    }

    private fun updateGallery(data: List<VersionedListItem>) {
        if(!::galleryGridAdapter.isInitialized) {
            galleryGridLayoutManager = GridLayoutManager(context, 4)

            galleryGridAdapter = GalleryGridAdapter(viewModel)
            galleryGridAdapter.setHasStableIds(false)

            galleryList.isSaveEnabled = false
            galleryList.itemAnimator = null
            galleryList.layoutManager = galleryGridLayoutManager
            galleryList.adapter = galleryGridAdapter
        }

        galleryGridAdapter.update(data)
    }

    private fun requestResultImage() = viewModel.processResultImage(previewImage.getImageInfo())

    private fun passResult(imageFile: File, profileItem: ProfileItem) {
        targetFragment!!.onActivityResult(
            REQUEST,
            Activity.RESULT_OK,
            Intent().apply {
                this.putExtra(RESULT, Result(imageFile.absolutePath, profileItem))
            })
    }
}