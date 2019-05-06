package io.golos.cyber_android.ui.screens.profile.edit.cover

import androidx.arch.core.util.Function
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.Compressor
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.images.ImageUploadUseCase
import io.golos.domain.interactors.model.UploadedImageModel
import io.golos.domain.interactors.model.UploadedImagesModel
import io.golos.domain.map
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class EditProfileCoverViewModel(
    private val imageUploadUseCase: ImageUploadUseCase,
    val dispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val uiScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
    private val workerScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    val uploadingStateLiveData = imageUploadUseCase.getAsLiveData
        .map(Function<UploadedImagesModel, QueryResult<UploadedImageModel>> {
            return@Function it.map[lastFile?.absolutePath ?: ""]
        })

    private var lastFile: File? = null

    init {
        imageUploadUseCase.subscribe()
    }

    fun uploadFile(file: File, panX: Float, panY: Float) {
        uiScope.launch {
            val compressedFile = withContext(workerScope.coroutineContext) {
                Compressor.compressImageFile(file, panX, panY, 0f)
            }
            imageUploadUseCase.submitImageForUpload(compressedFile.absolutePath)
            lastFile = compressedFile
        }

    }

    override fun onCleared() {
        super.onCleared()
        imageUploadUseCase.unsubscribe()
    }
}
