package io.golos.data.repositories.images_uploading

import io.golos.data.api.image_upload.ImageUploadApi
import io.golos.data.utils.ImageCompressor
import io.golos.domain.DispatchersProvider
import io.golos.domain.requestmodel.CompressionParams
import io.golos.domain.requestmodel.ImageUploadRequest
import kotlinx.coroutines.withContext

abstract class ImageUploadRepositoryBase(
    private val dispatchersProvider: DispatchersProvider,
    private val api: ImageUploadApi,
    private val compressor: ImageCompressor
) {
    protected suspend fun uploadImage(params: ImageUploadRequest): String =
        withContext(dispatchersProvider.ioDispatcher) {
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
}