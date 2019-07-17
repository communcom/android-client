package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.data.api.ImageUploadApi
import io.golos.data.utils.ImageCompressor
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.UploadedImageEntity
import io.golos.domain.entities.UploadedImagesEntity
import io.golos.domain.requestmodel.CompressionParams
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.ImageUploadRequest
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.*
import java.io.File

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-29.
 */
class ImageUploadRepository(
    private val api: ImageUploadApi,
    private val dispatchersProvider: DispatchersProvider,
    private val compressor: ImageCompressor,
    private val logger: Logger
) : Repository<UploadedImagesEntity, ImageUploadRequest> {
    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val uploadedImages = MutableLiveData<UploadedImagesEntity>()
    private val uploadedUpdateStates = MutableLiveData<Map<Identifiable.Id, QueryResult<ImageUploadRequest>>>()

    private val allRequest = ImageUploadRequest(File("\\"), CompressionParams.DirectCompressionParams)
    private val jobsMap: HashMap<ImageUploadRequest, Job> = hashMapOf()

    override val allDataRequest: ImageUploadRequest = allRequest
    override fun getAsLiveData(params: ImageUploadRequest): LiveData<UploadedImagesEntity> {
        return uploadedImages
    }

    override fun makeAction(params: ImageUploadRequest) {
        if (params == allRequest) return
        repositoryScope.launch {
            try {
                uploadedUpdateStates.value =
                    uploadedUpdateStates.value.orEmpty() + (params.id to QueryResult.Loading(params))

                val result = withContext(dispatchersProvider.workDispatcher) {

                    val compressedFile = when (params.compressionParams) {
                        is CompressionParams.DirectCompressionParams -> compressor.compressImageFile(params.imageFile)
                        is CompressionParams.AbsoluteCompressionParams ->
                            compressor.compressImageFile(
                                params.imageFile,
                                (params.compressionParams as CompressionParams.AbsoluteCompressionParams).transX,
                                (params.compressionParams as CompressionParams.AbsoluteCompressionParams).transY,
                                (params.compressionParams as CompressionParams.AbsoluteCompressionParams).rotation
                            )
                        is CompressionParams.RelativeCompressionParams ->
                            compressor.compressImageFile(
                                params.imageFile,
                                (params.compressionParams as CompressionParams.RelativeCompressionParams).paddingXPercent,
                                (params.compressionParams as CompressionParams.RelativeCompressionParams).paddingYPercent,
                                (params.compressionParams as CompressionParams.RelativeCompressionParams).requiredWidthPercent,
                                (params.compressionParams as CompressionParams.RelativeCompressionParams).requiredHeightPercent
                            )
                    }

                    api.uploadImage(compressedFile)
                }
                uploadedImages.value =
                    UploadedImagesEntity(
                        uploadedImages.value?.imageUrls.orEmpty() + (params.imageFile.absolutePath to UploadedImageEntity(
                            result
                        ))
                    )

                uploadedUpdateStates.value =
                    uploadedUpdateStates.value.orEmpty() + (params.id to QueryResult.Success(params))

            } catch (e: Exception) {
                logger(e)
                uploadedUpdateStates.value =
                    uploadedUpdateStates.value.orEmpty() + (params.id to QueryResult.Error(e, params))
            }
        }.let {
            jobsMap[params]?.cancel()
            jobsMap[params] = it
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<ImageUploadRequest>>>
        get() = uploadedUpdateStates
}