package io.golos.data.repositories.images_uploading

import io.golos.data.api.image_upload.ImageUploadApi
import io.golos.data.utils.ImageCompressor
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UploadedImageEntity
import io.golos.domain.requestmodel.ImageUploadRequest
import javax.inject.Inject

class ImageUploadRepositoryImpl
@Inject
constructor(
    api: ImageUploadApi,
    dispatchersProvider: DispatchersProvider,
    compressor: ImageCompressor
) : ImageUploadRepositoryBase(dispatchersProvider, api, compressor),
    ImageUploadRepository {

    override suspend fun upload(params: ImageUploadRequest): UploadedImageEntity = UploadedImageEntity(uploadImage(params))
}