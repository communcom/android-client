package io.golos.data.repositories.images_uploading

import io.golos.cyber4j.sharedmodel.Either
import io.golos.data.api.ImageUploadApi
import io.golos.data.utils.ImageCompressor
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.entities.UploadedImageEntity
import io.golos.domain.requestmodel.ImageUploadRequest
import javax.inject.Inject

class ImageUploadRepositoryImpl
@Inject
constructor(
    api: ImageUploadApi,
    dispatchersProvider: DispatchersProvider,
    compressor: ImageCompressor,
    private val logger: Logger
) : ImageUploadRepositoryBase(dispatchersProvider, api, compressor),
    ImageUploadRepository {

    override suspend fun upload(params: ImageUploadRequest): Either<UploadedImageEntity, Throwable> =
        try {
            Either.Success<UploadedImageEntity, Throwable>(UploadedImageEntity(uploadImage(params)))
        } catch (ex: Exception) {
            logger.log(ex)
            Either.Failure<UploadedImageEntity, Throwable>(ex)
        }
}