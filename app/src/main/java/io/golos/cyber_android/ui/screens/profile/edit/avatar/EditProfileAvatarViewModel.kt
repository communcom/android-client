package io.golos.cyber_android.ui.screens.profile.edit.avatar

import io.golos.cyber_android.ui.screens.profile.edit.EditProfileViewModelBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.images.ImageUploadUseCase
import io.golos.domain.interactors.model.UploadedImagesModel
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.extensions.map
import io.golos.domain.requestmodel.CompressionParams
import java.io.File
import javax.inject.Inject

class EditProfileAvatarViewModel
@Inject
constructor(
    private val userMetadataUseCase: UserMetadataUseCase,
    private val imageUploadUseCase: UseCase<UploadedImagesModel>,
    val dispatchersProvider: DispatchersProvider
) : EditProfileViewModelBase(userMetadataUseCase) {

    /**
     * State of uploading image to remote server
     */
    val getFileUploadingStateLiveData = imageUploadUseCase.getAsLiveData
        .map {
            it?.map?.get(lastFile?.absolutePath ?: "")
        }

    private var lastFile: File? = null

    init {
        imageUploadUseCase.subscribe()
    }

    /**
     * Uploads image file to remote server. Image will be compressed and cropped to square before that.
     * @param file image file
     * @param transX padding on x axis
     * @param transY padding on y axis
     * @param rotation degrees on which image should be rotated
     */
    fun uploadFile(file: File, transX: Float, transY: Float, rotation: Float) {
        (imageUploadUseCase as ImageUploadUseCase).submitImageForUpload(
            file.absolutePath,
            CompressionParams.AbsoluteCompressionParams(transX, transY, rotation)
        )
        lastFile = file
    }

    override fun onCleared() {
        super.onCleared()
        imageUploadUseCase.unsubscribe()
    }

    /**
     * Updates user avatar in user metadata
     */
    fun updateAvatar(url: String, waitForTransaction: Boolean = true) {
        userMetadataUseCase.updateMetadata(newProfileImageUrl = url, shouldWaitForTransaction = waitForTransaction)
    }
}
