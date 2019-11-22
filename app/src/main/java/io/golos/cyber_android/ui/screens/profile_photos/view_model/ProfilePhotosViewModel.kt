package io.golos.cyber_android.ui.screens.profile_photos.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.dto.PhotoPlace
import io.golos.cyber_android.ui.screens.profile_photos.dto.InitPhotoPreviewCommand
import io.golos.cyber_android.ui.screens.profile_photos.model.ProfilePhotosModel
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
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
) : ViewModelBase<ProfilePhotosModel>(dispatchersProvider, model) {

    private val _imagePreviewVisibility = MutableLiveData<Int>(View.INVISIBLE)
    val imagePreviewVisibility: LiveData<Int> get() = _imagePreviewVisibility

    private val _imagePreviewLoadingVisibility = MutableLiveData<Int>(View.VISIBLE)
    val imagePreviewLoadingVisibility: LiveData<Int> get() = _imagePreviewLoadingVisibility

    fun start() {
        _command.value = InitPhotoPreviewCommand(photoPlace, imageUrl)
    }

    fun onCancelClick() {
        _command.value = BackCommand()
    }

    fun onPreviewLoadingCompleted() {
        _imagePreviewVisibility.value = View.VISIBLE
        _imagePreviewLoadingVisibility.value = View.INVISIBLE
    }
}