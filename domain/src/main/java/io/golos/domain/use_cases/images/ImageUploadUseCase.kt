package io.golos.domain.use_cases.images

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.dto.UploadedImagesEntity
import io.golos.domain.extensions.distinctUntilChanged
import io.golos.domain.repositories.Repository
import io.golos.domain.requestmodel.ImageUploadRequest
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.model.UploadedImageModel
import io.golos.domain.use_cases.model.UploadedImagesModel
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-29.
 */
class ImageUploadUseCase
@Inject
constructor(
    private val imagesRepository: Repository<UploadedImagesEntity, ImageUploadRequest>
) : UseCase<UploadedImagesModel> {
    private val uploadedImagesLiveData = MutableLiveData<UploadedImagesModel>()

    override val getAsLiveData: LiveData<UploadedImagesModel> = uploadedImagesLiveData.distinctUntilChanged()

    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    override fun subscribe() {
        super.subscribe()
        mediator.observeForever(observer)
        mediator.addSource(imagesRepository.updateStates) { imageStatesMap ->
            imageStatesMap ?: return@addSource

            val uploadedImages =
                imagesRepository.getAsLiveData(imagesRepository.allDataRequest).value ?: UploadedImagesEntity(
                    emptyMap()
                )

            val newState = imageStatesMap
                .mapKeys { (it.key as ImageUploadRequest.Id)._imageFile.absolutePath }
                .mapValues { mapEntry ->
                    val value = mapEntry.value
                    when (value) {
                        is QueryResult.Loading -> QueryResult.Loading(UploadedImageModel.empty)
                        is QueryResult.Error -> QueryResult.Error(value.error, UploadedImageModel.empty)
                        is QueryResult.Success -> QueryResult.Success(
                            UploadedImageModel(
                                uploadedImages.imageUrls.getValue(value.originalQuery.imageFile.absolutePath).url
                            )
                        )
                    }
                }.run { UploadedImagesModel(this) }
            uploadedImagesLiveData.value = newState
        }
        mediator.addSource(imagesRepository.getAsLiveData(imagesRepository.allDataRequest)) {

        }
    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeObserver(observer)
        mediator.removeSource(imagesRepository.updateStates)
        mediator.removeSource(imagesRepository.getAsLiveData(imagesRepository.allDataRequest))
    }

}