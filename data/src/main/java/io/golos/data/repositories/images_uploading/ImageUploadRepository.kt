package io.golos.data.repositories.images_uploading

import io.golos.cyber4j.sharedmodel.Either
import io.golos.domain.entities.UploadedImageEntity
import io.golos.domain.requestmodel.ImageUploadRequest

interface ImageUploadRepository {
    suspend fun upload(params: ImageUploadRequest): Either<UploadedImageEntity, Throwable>
}