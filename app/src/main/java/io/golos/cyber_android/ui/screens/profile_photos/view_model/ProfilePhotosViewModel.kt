package io.golos.cyber_android.ui.screens.profile_photos.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.PhotoPlace
import io.golos.cyber_android.ui.screens.profile_photos.dto.InitPhotoPreviewCommand
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoGridItem
import io.golos.cyber_android.ui.screens.profile_photos.model.ProfilePhotosModel
import io.golos.cyber_android.ui.screens.profile_photos.view.grid.GalleryGridItemEventsProcessor
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ProfilePhotosViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: ProfilePhotosModel,
    private val photoPlace: PhotoPlace,
    @Named(Clarification.IMAGE_URL)
    private val imageUrl: String?
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
                    _command.value = InitPhotoPreviewCommand(photoPlace, (items[1] as PhotoGridItem).imageUri)
                }

                _command.value = SetLoadingVisibilityCommand(false)
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = ShowMessageCommand(R.string.common_general_error)
                _command.value = BackCommand()
            }
        }
    }

    fun onCancelClick() {
        _command.value = BackCommand()
    }

    fun onPreviewLoadingCompleted() {
        _imagePreviewVisibility.value = View.VISIBLE
        _imagePreviewLoadingVisibility.value = View.INVISIBLE
    }

    fun onStoragePermissionDenied() {
        _command.value = ShowMessageCommand(R.string.profile_photos_storage_permissions_denied)
        _command.value = BackCommand()
    }

    override fun onCameraCellClick() {
        // do noting
    }

    override fun onPhotoCellClick(imageUri: String) {
        // do nothing
    }
}