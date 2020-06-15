package io.golos.cyber_android.ui.screens.post_edit.fragment.model

import android.net.Uri
import com.squareup.moshi.Moshi
import dagger.Lazy
import io.golos.commun4j.services.model.OEmbedResult
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.utils.toCyberName
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ExternalLinkError
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ExternalLinkType
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ValidationResult
import io.golos.cyber_android.ui.shared.broadcast_actions_registries.PostCreateEditRegistry
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.utils.localSize
import io.golos.data.errors.CyberServicesError
import io.golos.data.mappers.mapToBlockEntity
import io.golos.data.repositories.embed.EmbedRepository
import io.golos.data.repositories.images_uploading.ImageUploadRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.UploadedImageEntity
import io.golos.domain.dto.block.ContentBlockEntity
import io.golos.domain.dto.block.ListContentBlockEntity
import io.golos.domain.posts_parsing_rendering.mappers.editor_output_to_json.EditorOutputToJsonMapper
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.*
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.requestmodel.CompressionParams
import io.golos.domain.requestmodel.ImageUploadRequest
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ImageBlock
import io.golos.posts_editor.utilities.post.PostStubs
import io.golos.utils.id.IdUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.net.URI
import javax.inject.Inject

class EditorPageModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val embedRepository: EmbedRepository,
    private val imageUploadRepository: ImageUploadRepository,
    private val discussionRepository: DiscussionRepository,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val moshi: Moshi,
    private val postCreateEditRegistry: PostCreateEditRegistry
) : ModelBaseImpl(), EditorPageModel {

    override suspend fun getExternalLinkInfo(uri: String): Either<ExternalLinkInfo, ExternalLinkError> =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val linkInfo = mapExternalLinkInfo(embedRepository.getOEmbedEmbed(uri)!!, uri)
                if (linkInfo == null) {
                    Either.Failure<ExternalLinkInfo, ExternalLinkError>(ExternalLinkError.TYPE_IS_NOT_SUPPORTED)
                } else {
                    Either.Success<ExternalLinkInfo, ExternalLinkError>(linkInfo)
                }
            } catch (ex: CyberServicesError) {
                Timber.e(ex)
                Either.Failure<ExternalLinkInfo, ExternalLinkError>(ExternalLinkError.INVALID_URL)
            } catch (ex: Exception) {
                Timber.e(ex)
                Either.Failure<ExternalLinkInfo, ExternalLinkError>(ExternalLinkError.GENERAL_ERROR)
            }
        }

    override fun validatePost(title: String, content: List<ControlMetadata>): ValidationResult {
        if (content.isEmpty()) {
            return ValidationResult.ERROR_POST_IS_EMPTY
        }

        val postLen = content
            .filterIsInstance<ParagraphMetadata>()
            .sumBy { it.plainText.length }

        if (postLen > io.golos.utils.PostConstants.MAX_POST_CONTENT_LENGTH) {
            return ValidationResult.ERROR_POST_IS_TOO_LONG
        }

        return ValidationResult.SUCCESS
    }

    /**
     * @return null if no image to upload otherwise - operation contentId
     */
    @Suppress("IfThenToElvis")
    override suspend fun uploadLocalImage(content: List<ControlMetadata>): UploadedImageEntity? =
        withContext(dispatchersProvider.ioDispatcher) {
            val firstLocalImage = content.firstOrNull {
                it is EmbedMetadata && it.type == EmbedType.LOCAL_IMAGE
            }

            if (firstLocalImage == null) {
                return@withContext null
            } else {
                firstLocalImage
                    .let { metadata -> (metadata as EmbedMetadata).sourceUri }
                    .let { uri ->
                        val file = File(URI.create(uri.toString()))
                        imageUploadRepository.upload(
                            ImageUploadRequest(file, uri, CompressionParams.DirectCompressionParams)
                        )
                    }
            }
        }

    override suspend fun createPost(
        content: List<ControlMetadata>,
        adultOnly: Boolean,
        communityId: CommunityIdDomain,
        localImagesUri: List<String>
    ): ContentIdDomain {
        var body = EditorOutputToJsonMapper.mapPost(content, localImagesUri)
        if (localImagesUri.isNotEmpty()) {
            val adapter = moshi.adapter(ListContentBlockEntity::class.java)
            val listContentBlockEntity = adapter.fromJson(body)
            val contentBlockEntityList: MutableList<ContentBlockEntity> = listContentBlockEntity!!.content.toMutableList()
            val imageBlockList = localImagesUri.map { uri ->
                val imageUri = Uri.parse(uri)
                val imageSize = imageUri.localSize()
                ImageBlock(
                    IdUtil.generateLongId(),
                    imageUri,
                    null,
                    imageSize.x,
                    imageSize.y
                )
            }
            contentBlockEntityList.add(ContentBlockEntity(IdUtil.generateLongId(), "attachments", imageBlockList.mapToBlockEntity()))
            listContentBlockEntity.copy(content = contentBlockEntityList)
            body = adapter.toJson(listContentBlockEntity)
        }
        val tags = extractTags(content, adultOnly)
        val contentId = discussionRepository.createPost(communityId, body, tags.toList())

        postCreateEditRegistry.setPostCreated(contentId)

        return contentId
    }

    override suspend fun updatePost(
        contentIdDomain: ContentIdDomain,
        content: List<ControlMetadata>,
        permlink: Permlink,
        adultOnly: Boolean,
        localImagesUri: List<String>
    ): ContentIdDomain {
        var body = EditorOutputToJsonMapper.mapPost(content, localImagesUri)
        val tags = extractTags(content, adultOnly).toList()
        if (localImagesUri.isNotEmpty()) {
            val adapter = moshi.adapter(ListContentBlockEntity::class.java)
            val listContentBlockEntity = adapter.fromJson(body)
            val contentBlockEntityList: MutableList<ContentBlockEntity> = listContentBlockEntity!!.content.toMutableList()
            val imageBlockList = localImagesUri.map { uri ->
                val imageUri = Uri.parse(uri)
                val imageSize = imageUri.localSize()
                ImageBlock(
                    IdUtil.generateLongId(),
                    Uri.parse(uri),
                    null,
                    imageSize.x,
                    imageSize.y
                )
            }
            contentBlockEntityList.add(ContentBlockEntity(IdUtil.generateLongId(), "attachments", imageBlockList.mapToBlockEntity()))
            listContentBlockEntity.copy(content = contentBlockEntityList)
            body = adapter.toJson(listContentBlockEntity)
        }
        return discussionRepository.updatePost(contentIdDomain, body, tags)
    }

    override suspend fun getPostToEdit(permlink: Permlink): PostModel =
        withContext(dispatchersProvider.ioDispatcher) {
            delay(500)
            discussionRepository.getPost(CyberName(currentUserRepository.authState!!.user.userId), permlink)
        }

    override suspend fun getPostToEdit(contentId: ContentId): PostDomain =
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.getPost(
                contentId.userId.toCyberName(),
                contentId.communityId,
                contentId.permlink
            )
        }

    /**
     * @return null - this type of link is not supported
     */
    private fun mapExternalLinkInfo(serverLinkInfo: OEmbedResult, sourceUrl: String): ExternalLinkInfo? {
        val type = when (serverLinkInfo.type) {
            "link", "website" -> ExternalLinkType.WEBSITE

            "image", "photo" -> ExternalLinkType.IMAGE

            "video" -> ExternalLinkType.VIDEO

            else -> {
                Timber.e(UnsupportedOperationException("This resource type is not supported: ${serverLinkInfo.type}"))
                null
            }
        }
            ?: return null

        val thumbnailUrl = when (type) {
            ExternalLinkType.VIDEO -> serverLinkInfo.thumbnail_url ?: PostStubs.video
            ExternalLinkType.WEBSITE -> serverLinkInfo.thumbnail_url ?: PostStubs.website
            ExternalLinkType.IMAGE -> sourceUrl
        }

        return ExternalLinkInfo(
            type,
            serverLinkInfo.description ?: serverLinkInfo.title ?: sourceUrl,
            Uri.parse(thumbnailUrl),
            Uri.parse(sourceUrl)
        )
    }

    private fun extractTags(content: List<ControlMetadata>, adultOnly: Boolean): Set<String> {
        val tags = content
            .asSequence()
            .filterIsInstance<ParagraphMetadata>()
            .map { it.spans }
            .flatten()
            .filterIsInstance<TagSpanInfo>()
            .map { it.displayValue }
            .toMutableSet()

        if (adultOnly) {
            tags.add("nsfw")
        }

        return tags
    }
}