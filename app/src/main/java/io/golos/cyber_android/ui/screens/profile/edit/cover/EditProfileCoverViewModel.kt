package io.golos.cyber_android.ui.screens.profile.edit.cover

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

class EditProfileCoverViewModel(
    private val userMetadataUseCase: UserMetadataUseCase,
    private val imageUploadUseCase: ImageUploadUseCase,
    val dispatchersProvider: DispatchersProvider
) : BaseEditProfileViewModel(userMetadataUseCase) {

    private val uiScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
    private val workerScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    val getFileUploadingStateLiveData = imageUploadUseCase.getAsLiveData
        .map(Function<UploadedImagesModel, QueryResult<UploadedImageModel>> {
            return@Function it.map[lastFile?.absolutePath ?: ""]
        })

    private var lastFile: File? = null

    init {
        imageUploadUseCase.subscribe()
    }

    fun uploadFile(file: File, x: Float, y: Float, width: Float, height: Float) {
        uiScope.launch {
            val compressedFile = withContext(workerScope.coroutineContext) {
                Compressor.compressImageFile(file, x, y, width, height)
            }
            imageUploadUseCase.submitImageForUpload(compressedFile.absolutePath)
            lastFile = compressedFile
        }
    }

    fun updateCover(imageUrl: String) {
        userMetadataUseCase.updateMetadata(newCoverUrl = imageUrl)
    }

    override fun onCleared() {
        super.onCleared()
        imageUploadUseCase.unsubscribe()
    }
}
