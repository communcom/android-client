package io.golos.data.repositories.images_uploading

import io.golos.domain.dto.UploadedImageEntity
import io.golos.domain.requestmodel.ImageUploadRequest

interface ImageUploadRepository {
    suspend fun upload(params: ImageUploadRequest): UploadedImageEntity
}