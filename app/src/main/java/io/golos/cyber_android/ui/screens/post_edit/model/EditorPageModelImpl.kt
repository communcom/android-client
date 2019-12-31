package io.golos.cyber_android.ui.screens.post_edit.model

import android.net.Uri
import com.squareup.moshi.Moshi
import io.golos.commun4j.services.model.OEmbedResult
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.utils.toCyberName
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_edit.dto.ExternalLinkError
import io.golos.cyber_android.ui.screens.post_edit.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.screens.post_edit.dto.ExternalLinkType
import io.golos.cyber_android.ui.screens.post_edit.dto.ValidationResult
import io.golos.data.api.communities.CommunitiesApi
import io.golos.data.api.embed.EmbedApi
import io.golos.data.dto.block.ContentBlockEntity
import io.golos.data.dto.block.ListContentBlockEntity
import io.golos.data.errors.CyberServicesError
import io.golos.data.mappers.mapToBlockEntity
import io.golos.data.repositories.images_uploading.ImageUploadRepository
import io.golos.domain.DispatchersProvider
import io.golos.data.persistence.key_value_storage.KeyValueStorageFacade
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.UploadedImageEntity
import io.golos.domain.posts_parsing_rendering.PostGlobalConstants
import io.golos.domain.posts_parsing_rendering.mappers.editor_output_to_json.EditorOutputToJsonMapper
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.requestmodel.CompressionParams
import io.golos.domain.requestmodel.ImageUploadRequest
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.post.editor_output.*
import io.golos.domain.use_cases.post.post_dto.ImageBlock
import io.golos.posts_editor.utilities.post.PostStubs
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
    private val embedApi: EmbedApi,
    private val communityApi: CommunitiesApi,
    private val imageUploadRepository: ImageUploadRepository,
    private val discussionRepository: DiscussionRepository,
    private val keyValueStorage: KeyValueStorageFacade,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val moshi: Moshi
) : ModelBaseImpl(), EditorPageModel {

    override suspend fun getExternalLinkInfo(uri: String): Either<ExternalLinkInfo, ExternalLinkError> =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val linkInfo = mapExternalLinkInfo(embedApi.getOEmbedEmbed(uri)!!, uri)
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
        communityId: CommunityId,
        localImagesUri: List<String>
    ): ContentIdDomain {
        var body = EditorOutputToJsonMapper.map(content, localImagesUri)
        if (localImagesUri.isNotEmpty()) {
            val adapter = moshi.adapter(ListContentBlockEntity::class.java)
            val listContentBlockEntity = adapter.fromJson(body)
            val contentBlockEntityList: MutableList<ContentBlockEntity> = listContentBlockEntity!!.content.toMutableList()
            val blockVersion = PostGlobalConstants.postFormatVersion.toString()
            val imageBlockList = localImagesUri.map { uri ->
                ImageBlock(blockVersion, Uri.parse(uri), null)
            }
            contentBlockEntityList.add(ContentBlockEntity(blockVersion, "attachments", imageBlockList.mapToBlockEntity()))
            listContentBlockEntity.copy(content = contentBlockEntityList)
            body = adapter.toJson(listContentBlockEntity)
        }
        val tags = extractTags(content, adultOnly)
        return discussionRepository.createPost(communityId.id, body, tags.toList())
    }

    override suspend fun updatePost(
        contentIdDomain: ContentIdDomain,
        content: List<ControlMetadata>,
        permlink: Permlink,
        adultOnly: Boolean,
        localImagesUri: List<String>
    ): ContentIdDomain {
        var body = EditorOutputToJsonMapper.map(content, localImagesUri)
        val tags = extractTags(content, adultOnly).toList()
        if (localImagesUri.isNotEmpty()) {
            val adapter = moshi.adapter(ListContentBlockEntity::class.java)
            val listContentBlockEntity = adapter.fromJson(body)
            val contentBlockEntityList: MutableList<ContentBlockEntity> = listContentBlockEntity!!.content.toMutableList()
            val blockVersion = PostGlobalConstants.postFormatVersion.toString()
            val imageBlockList = localImagesUri.map { uri ->
                ImageBlock(blockVersion, Uri.parse(uri), null)
            }
            contentBlockEntityList.add(ContentBlockEntity(blockVersion, "attachments", imageBlockList.mapToBlockEntity()))
            listContentBlockEntity.copy(content = contentBlockEntityList)
            body = adapter.toJson(listContentBlockEntity)
        }
        return discussionRepository.updatePost(contentIdDomain, body, tags)
    }

    override suspend fun getLastUsedCommunity(): CommunityDomain? =
        withContext(dispatchersProvider.ioDispatcher) {
            delay(500)
            keyValueStorage.getLastUsedCommunityId()?.let { communityApi.getCommunityById(it) }
        }

    override suspend fun saveLastUsedCommunity(community: CommunityDomain) {
        withContext(dispatchersProvider.ioDispatcher) {
            keyValueStorage.saveLastUsedCommunityId(community.communityId)
        }
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
            "link" -> ExternalLinkType.WEBSITE
            "photo" -> ExternalLinkType.IMAGE
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
            .map { it.value }
            .toMutableSet()

        if (adultOnly) {
            tags.add("nsfw")
        }

        return tags
    }
}