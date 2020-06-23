package io.golos.cyber_android.ui.screens.profile_photos.view_model

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.*
import io.golos.cyber_android.ui.screens.profile_photos.model.ProfilePhotosModel
import io.golos.cyber_android.ui.screens.profile_photos.view.grid.GalleryGridItemEventsProcessor
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ProfilePhotosViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: ProfilePhotosModel,
    private val profileItem: ProfileItem
) : ViewModelBase<ProfilePhotosModel>(dispatchersProvider, model),
    GalleryGridItemEventsProcessor {

    private val _imagePreviewVisibility = MutableLiveData<Int>(View.INVISIBLE)
    val imagePreviewVisibility: LiveData<Int> get() = _imagePreviewVisibility

    private val _imagePreviewLoadingVisibility = MutableLiveData<Int>(View.INVISIBLE)
    val imagePreviewLoadingVisibility: LiveData<Int> get() = _imagePreviewLoadingVisibility

    private val _galleryListVisibility = MutableLiveData<Int>(View.INVISIBLE)
    val galleryListVisibility: LiveData<Int> get() = _galleryListVisibility

    private val _galleryItems = MutableLiveData<List<VersionedListItem>>()
    val galleryItems: LiveData<List<VersionedListItem>> get() = _galleryItems

    fun start() {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)

                val items = model.createGallery()         // Load gallery items
                _galleryItems.value = items

                _galleryListVisibility.value = View.VISIBLE
                _imagePreviewLoadingVisibility.value = View.VISIBLE

                if(items.size > 1) {
                    _command.value = InitPhotoPreviewCommand(profileItem, (items[1] as PhotoGridItem).imageUri, false)
                }

                _command.value = SetLoadingVisibilityCommand(false)
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
                _command.value = NavigateBackwardCommand()
            }
        }
    }

    fun onCancelClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onSaveClick() {
        _command.value = RequestResultImageCommand()
    }

    fun processResultImage(imageInfo: PhotoViewImageInfo) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)

                val imageFile = model.saveSelectedPhoto(imageInfo, profileItem != ProfileItem.COMMENT)

                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = PassResultCommand(imageFile, profileItem)
                _command.value = NavigateBackwardCommand()
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }

    fun onPreviewLoadingCompleted() {
        _imagePreviewVisibility.value = View.VISIBLE
        _imagePreviewLoadingVisibility.value = View.INVISIBLE
    }

    fun onStoragePermissionDenied() {
        _command.value = ShowMessageResCommand(R.string.profile_photos_storage_permissions_denied)
        _command.value = NavigateBackwardCommand()
    }

    fun onCameraImageCaptured(cameraImageUri: Uri) {
        try {
            _galleryItems.value = model.addCameraImageToGallery(cameraImageUri)
            selectPhoto(cameraImageUri.toString(), true)
        } catch (ex: Exception) {
            _command.value = ShowMessageResCommand(R.string.common_general_error)
        }
    }

    override fun onCameraCellClick() {
        _command.value = StartCameraCommand()
    }

    override fun onPhotoCellClick(selectedImageUri: String, isImageFromCamera: Boolean)
            = selectPhoto(selectedImageUri, isImageFromCamera)

    private fun selectPhoto(selectedImageUri: String, isImageFromCamera: Boolean) {
        _imagePreviewVisibility.value = View.INVISIBLE
        _imagePreviewLoadingVisibility.value = View.VISIBLE

        _command.value = InitPhotoPreviewCommand(profileItem, selectedImageUri, isImageFromCamera)
    }
}