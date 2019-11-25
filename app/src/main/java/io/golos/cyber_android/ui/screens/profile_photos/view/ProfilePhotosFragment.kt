package io.golos.cyber_android.ui.screens.profile_photos.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_photos.ProfilePhotosFragmentComponent
import io.golos.cyber_android.core.camera.CameraHelper
import io.golos.cyber_android.databinding.FragmentProfilePhotosBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.PhotoPlace
import io.golos.cyber_android.ui.screens.profile_photos.dto.InitPhotoPreviewCommand
import io.golos.cyber_android.ui.screens.profile_photos.dto.StartCameraCommand
import io.golos.cyber_android.ui.screens.profile_photos.view.grid.GalleryGridAdapter
import io.golos.cyber_android.ui.screens.profile_photos.view_model.ProfilePhotosViewModel
import kotlinx.android.synthetic.main.fragment_profile_photos.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import javax.inject.Inject

@RuntimePermissions
class ProfilePhotosFragment : FragmentBaseMVVM<FragmentProfilePhotosBinding, ProfilePhotosViewModel>() {

    private lateinit var galleryGridAdapter: GalleryGridAdapter
    private lateinit var galleryGridLayoutManager: GridLayoutManager

    @Inject
    internal lateinit var cameraHelper: CameraHelper

    companion object {
        private const val PLACE = "PLACE"
        private const val IMAGE_URL = "IMAGE_URL"

        fun newInstance(place: PhotoPlace, imageUrl: String?): ProfilePhotosFragment {
            return ProfilePhotosFragment().apply {
                arguments = Bundle().apply {
                    putInt(PLACE, place.value)
                    putString(IMAGE_URL, imageUrl)
                }
            }
        }
    }

    override fun provideViewModelType(): Class<ProfilePhotosViewModel> = ProfilePhotosViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_photos

    override fun inject() =
        App.injections
            .get<ProfilePhotosFragmentComponent>(
                PhotoPlace.create(arguments!!.getInt(PLACE)),
                arguments!!.getString(IMAGE_URL))
            .inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfilePhotosFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfilePhotosBinding, viewModel: ProfilePhotosViewModel) {
        binding.viewModel = viewModel
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is BackCommand -> requireActivity().onBackPressed()
            is InitPhotoPreviewCommand -> initPhotoPreview(command.photoPlace, command.imageUrl, command.isImageFromCamera)
            is StartCameraCommand -> startCameraWithPermissionCheck()
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

    private fun initPhotoPreview(place: PhotoPlace, imageUrl: String?, isImageFromCamera: Boolean) {
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
}