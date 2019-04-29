package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.data.api.ImageUploadApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.UploadedImageEntity
import io.golos.domain.entities.UploadedImagesEntity
import io.golos.domain.model.Identifiable
import io.golos.domain.model.ImageUploadRequest
import io.golos.domain.model.QueryResult
import kotlinx.coroutines.*
import java.io.File

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-29.
 */
class ImageUploadRepository(
    private val api: ImageUploadApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger
) : Repository<UploadedImagesEntity, ImageUploadRequest> {
    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val uploadedImages = MutableLiveData<UploadedImagesEntity>()
    private val uploadedUpdateStates = MutableLiveData<Map<Identifiable.Id, QueryResult<ImageUploadRequest>>>()

    private val allRequest = ImageUploadRequest(File("\\"))
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

                val result = withContext(dispatchersProvider.workDispatcher) { api.uploadImage(params.imageFile) }
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