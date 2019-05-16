package io.golos.cyber_android.ui.screens.profile.edit.avatar

import androidx.arch.core.util.Function
import io.golos.cyber_android.ui.screens.profile.edit.BaseEditProfileViewModel
import io.golos.cyber_android.utils.Compressor
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.images.ImageUploadUseCase
import io.golos.domain.interactors.model.UploadedImageModel
import io.golos.domain.interactors.model.UploadedImagesModel
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.map
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class EditProfileAvatarViewModel(
    private val userMetadataUseCase: UserMetadataUseCase,
    private val imageUploadUseCase: ImageUploadUseCase,
    val dispatchersProvider: DispatchersProvider
) : BaseEditProfileViewModel(userMetadataUseCase) {

    private val uiScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
    private val workerScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    /**
     * State of uploading image to remote server
     */
    val getFileUploadingStateLiveData = imageUploadUseCase.getAsLiveData
        .map(Function<UploadedImagesModel, QueryResult<UploadedImageModel>> {
            return@Function it.map[lastFile?.absolutePath ?: ""]
        })

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
        uiScope.launch {
            val compressedFile = withContext(workerScope.coroutineContext) {
                Compressor.compressImageFile(file, transX, transY, rotation, true)
            }
            imageUploadUseCase.submitImageForUpload(compressedFile.absolutePath)
            lastFile = compressedFile
        }

    }

    override fun onCleared() {
        super.onCleared()
        imageUploadUseCase.unsubscribe()
    }

    /**
     * Updates user avatar in user metadata
     */
    fun updateAvatar(url: String) {
        userMetadataUseCase.updateMetadata(newProfileImageUrl = url)
    }
}
