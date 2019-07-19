package io.golos.cyber_android.ui.screens.profile.edit.cover

import io.golos.cyber_android.ui.screens.profile.edit.EditProfileViewModelBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.images.ImageUploadUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.map
import io.golos.domain.requestmodel.CompressionParams
import java.io.File

class EditProfileCoverViewModel(
    private val userMetadataUseCase: UserMetadataUseCase,
    private val imageUploadUseCase: ImageUploadUseCase,
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
     * @param x padding on x axis in % of original image width
     * @param y padding on y axis in % of original image height
     * @param width required image width in % of original image width
     * @param height required image height in % of original image height
     */
    fun uploadFile(file: File, x: Float, y: Float, width: Float, height: Float) {
        imageUploadUseCase.submitImageForUpload(
            file.absolutePath,
            CompressionParams.RelativeCompressionParams(x, y, width, height)
        )
        lastFile = file
    }

    /**
     * Updates user cover in user metadata
     */
    fun updateCover(imageUrl: String) {
        userMetadataUseCase.updateMetadata(newCoverUrl = imageUrl)
    }

    override fun onCleared() {
        super.onCleared()
        imageUploadUseCase.unsubscribe()
    }
}
