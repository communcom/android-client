package io.golos.cyber_android.ui.shared_fragments.editor.model

import android.net.Uri
import android.util.Log
import io.golos.cyber4j.services.model.OEmbedResult
import io.golos.cyber4j.sharedmodel.Either
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkError
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkType
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ValidationResult
import io.golos.cyber_android.utils.PostConstants
import io.golos.data.api.EmbedApi
import io.golos.data.errors.CyberServicesError
import io.golos.data.repositories.images_uploading.ImageUploadRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.UploadedImageEntity
import io.golos.domain.post_editor.ControlMetadata
import io.golos.domain.post_editor.EmbedMetadata
import io.golos.domain.post_editor.EmbedType
import io.golos.domain.post_editor.ParagraphMetadata
import io.golos.domain.requestmodel.CompressionParams
import io.golos.domain.requestmodel.ImageUploadRequest
import io.golos.posts_parsing_rendering.metadata_to_json.MetadataToJsonMapper
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.UnsupportedOperationException
import java.net.URI
import javax.inject.Inject

class EditorPageModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val embedApi: EmbedApi,
    private val imageUploadRepository: ImageUploadRepository
) : ModelBaseImpl(), EditorPageModel {

    override suspend fun getExternalLinkInfo(uri: String): Either<ExternalLinkInfo, ExternalLinkError> =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val linkInfo = mapExternalLinkInfo(embedApi.getOEmbedEmbed(uri), uri)
                if(linkInfo == null) {
                    Either.Failure<ExternalLinkInfo, ExternalLinkError>(ExternalLinkError.TYPE_IS_NOT_SUPPORTED)
                } else {
                    Either.Success<ExternalLinkInfo, ExternalLinkError>(linkInfo)
                }
            } catch (ex: CyberServicesError) {
                App.logger.log(ex)
                Either.Failure<ExternalLinkInfo, ExternalLinkError>(ExternalLinkError.INVALID_URL)
            }
            catch (ex: Exception) {
                App.logger.log(ex)
                Either.Failure<ExternalLinkInfo, ExternalLinkError>(ExternalLinkError.GENERAL_ERROR)
            }
        }

    override fun validatePost(title: String, content: List<ControlMetadata>): ValidationResult {
        if(content.isEmpty()) {
            return ValidationResult.ERROR_POST_IS_EMPTY
        }

        val postLen = content
            .filterIsInstance<ParagraphMetadata>()
            .sumBy { it.plainText.length }

        if(postLen > PostConstants.MAX_POST_CONTENT_LENGTH) {
            return ValidationResult.ERROR_POST_IS_TOO_LONG
        }

        return ValidationResult.SUCCESS
    }

    /**
     * @return null if no image to upload otherwise - operation result
     */
    override suspend fun uploadLocalImage(content: List<ControlMetadata>): Either<UploadedImageEntity, Throwable>? =
        content
            .firstOrNull { it is EmbedMetadata && it.type == EmbedType.LOCAL_IMAGE }
            ?.let { metadata -> (metadata as EmbedMetadata).sourceUri }
            ?.let { uri -> File(URI.create(uri.toString())) }
            ?.let { file -> imageUploadRepository.upload(ImageUploadRequest(file, CompressionParams.DirectCompressionParams)) }

    override suspend fun createPost(content: List<ControlMetadata>, images: List<String>) {
        val json = MetadataToJsonMapper().map(content)
        Log.d("", "")
    }

    /**
     * @return null - this type of link is not supported
     */
    private fun mapExternalLinkInfo(serverLinkInfo: OEmbedResult, sourceUrl: String): ExternalLinkInfo? {
        val type  = when(serverLinkInfo.type) {
            "link" -> ExternalLinkType.WEBSITE
            "photo" -> ExternalLinkType.IMAGE
            "video" -> ExternalLinkType.VIDEO
            else -> {
                App.logger.log(UnsupportedOperationException("This resource type is not supported: ${serverLinkInfo.type}"))
                null
            }
        }
        ?: return null

        val thumbnailUrl = when(type) {
            ExternalLinkType.VIDEO -> serverLinkInfo.thumbnail_url ?: "file:///android_asset/video_stub.webp"
            ExternalLinkType.WEBSITE -> serverLinkInfo.thumbnail_url ?: "file:///android_asset/website_stub.webp"
            ExternalLinkType.IMAGE -> sourceUrl
        }

        return ExternalLinkInfo(
            type,
            serverLinkInfo.description ?: serverLinkInfo.title ?: sourceUrl,
            Uri.parse(thumbnailUrl),
            Uri.parse(sourceUrl))
    }
}