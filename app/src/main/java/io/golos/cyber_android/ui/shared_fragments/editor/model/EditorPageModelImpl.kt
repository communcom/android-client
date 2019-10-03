package io.golos.cyber_android.ui.shared_fragments.editor.model

import android.net.Uri
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
import io.golos.data.repositories.discussion_creation.DiscussionCreationRepository
import io.golos.data.repositories.images_uploading.ImageUploadRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.PostCreationResultEntity
import io.golos.domain.entities.UpdatePostResultEntity
import io.golos.domain.entities.UploadedImageEntity
import io.golos.domain.interactors.model.DiscussionCreationResultModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostCreationResultModel
import io.golos.domain.interactors.model.UpdatePostResultModel
import io.golos.domain.post.editor_output.TagSpanInfo
import io.golos.domain.post.editor_output.*
import io.golos.domain.requestmodel.CompressionParams
import io.golos.domain.requestmodel.ImageUploadRequest
import io.golos.domain.requestmodel.PostCreationRequestEntity
import io.golos.domain.requestmodel.PostUpdateRequestEntity
import io.golos.posts_editor.utilities.post.PostStubs
import io.golos.posts_parsing_rendering.editor_output_to_json.EditorOutputToJsonMapper
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
    private val imageUploadRepository: ImageUploadRepository,
    private val discussionCreationRepository: DiscussionCreationRepository
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

    override suspend fun createPost(
        content: List<ControlMetadata>,
        adultOnly: Boolean,
        localImagesUri: List<String>
    ): Either<DiscussionCreationResultModel, Throwable> {
        val postText = EditorOutputToJsonMapper().map(content, localImagesUri)

        val tags = extractTags(content, adultOnly)

        val postRequest = PostCreationRequestEntity("", postText, postText, tags.toList(), localImagesUri)

        return when(val creationResult = discussionCreationRepository.createOrUpdate(postRequest)) {
            is Either.Failure -> Either.Failure<DiscussionCreationResultModel, Throwable>(creationResult.value)
            is Either.Success -> {
                (creationResult.value as PostCreationResultEntity)
                .let {
                    Either.Success<DiscussionCreationResultModel, Throwable>(PostCreationResultModel(
                        DiscussionIdModel(it.postId.userId, it.postId.permlink)))
                }
            }
        }
    }

    override suspend fun updatePost(
        content: List<ControlMetadata>,
        permlink: String,
        adultOnly: Boolean,
        localImagesUri: List<String>
    ): Either<DiscussionCreationResultModel, Throwable> {
        val postText = EditorOutputToJsonMapper().map(content, localImagesUri)

        val tags = extractTags(content, adultOnly)

        val postRequest = PostUpdateRequestEntity(permlink, "", postText, postText, tags.toList(), localImagesUri)

        return when(val updateResult = discussionCreationRepository.createOrUpdate(postRequest)) {
            is Either.Failure -> Either.Failure<DiscussionCreationResultModel, Throwable>(updateResult.value)
            is Either.Success -> {
                (updateResult.value as UpdatePostResultEntity)
                    .let {
                        Either.Success<DiscussionCreationResultModel, Throwable>(UpdatePostResultModel(
                            DiscussionIdModel(it.id.userId, it.id.permlink)))
                    }
            }
        }
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
            ExternalLinkType.VIDEO -> serverLinkInfo.thumbnail_url ?: PostStubs.video
            ExternalLinkType.WEBSITE -> serverLinkInfo.thumbnail_url ?: PostStubs.website
            ExternalLinkType.IMAGE -> sourceUrl
        }

        return ExternalLinkInfo(
            type,
            serverLinkInfo.description ?: serverLinkInfo.title ?: sourceUrl,
            Uri.parse(thumbnailUrl),
            Uri.parse(sourceUrl))
    }

    private fun extractTags(content: List<ControlMetadata>, adultOnly: Boolean): Set<String> {
        val tags = content
            .asSequence()
            .filterIsInstance<ParagraphMetadata>()
            .map { it.spans }
            .flatten()
            .filterIsInstance<TagSpanInfo>()
            .map { it.value }
            .toMutableSet()

        if(adultOnly) {
            tags.add("nsfw")
        }

        return tags
    }
}