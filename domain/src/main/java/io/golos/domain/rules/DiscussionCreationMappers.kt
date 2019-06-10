package io.golos.domain.rules

import io.golos.cyber4j.model.*
import io.golos.cyber4j.utils.toCyberName
import io.golos.domain.Regexps
import io.golos.domain.entities.*
import io.golos.domain.requestmodel.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
class RequestEntityToArgumentsMapper : EntityToCyberMapper<DiscussionCreationRequestEntity, DiscussionCreateRequest> {

    override suspend fun invoke(entity: DiscussionCreationRequestEntity): DiscussionCreateRequest {
        val tags = HashSet<String>()
        val links = HashSet<String>()

        return when (entity) {
            is PostCreationRequestEntity -> {
                tags.addAll(entity.tags)
                extractHashTags(tags, entity.originalBody.toString())
                extractLinks(links, entity.originalBody.toString())

                CreatePostRequest(
                    entity.title,
                    entity.body,
                    tags.map { Tag(it) },
                    DiscussionCreateMetadata(links.map { DiscussionCreateMetadata.EmbedmentsUrl(it) }, emptyList()),
                    emptyList(),
                    true,
                    0
                )
            }
            is CommentCreationRequestEntity -> {
                tags.addAll(entity.tags)
                extractHashTags(tags, entity.body)
                extractLinks(links, entity.body)

                CreateCommentRequest(
                    entity.body,
                    entity.parentId.userId.toCyberName(),
                    entity.parentId.permlink,
                    tags.map { Tag(it) },
                    DiscussionCreateMetadata(links.map { DiscussionCreateMetadata.EmbedmentsUrl(it) }, emptyList()),
                    emptyList(),
                    true,
                    0
                )
            }
            is DeleteDiscussionRequestEntity -> {
                DeleteDiscussionRequest(entity.discussionPermlink)
            }
            is PostUpdateRequestEntity -> {
                tags.addAll(entity.tags)
                extractHashTags(tags, entity.originalBody.toString())
                extractLinks(links, entity.originalBody.toString())

                UpdatePostRequest(
                    entity.postPermlink,
                    entity.title, entity.body,
                    tags.map { Tag(it) },
                    DiscussionCreateMetadata(links.map { DiscussionCreateMetadata.EmbedmentsUrl(it) }, emptyList())
                )
            }
        }


    }

    private fun extractHashTags(toContainer: MutableCollection<String>, fromSource: String): MutableCollection<String> {

        Regexps.hashTagRegexp.findAll(fromSource, 0).forEach { matchResult ->

            matchResult.groupValues.firstOrNull()?.takeIf { it.isNotEmpty() }?.apply {

                toContainer.add(this.removePrefix("#"))
            }
        }
        return toContainer
    }

    private fun extractLinks(toContainer: MutableCollection<String>, fromSource: String): MutableCollection<String> {

        Regexps.link.findAll(fromSource, 0).forEach { matchResult ->

            matchResult.groupValues.firstOrNull()?.takeIf { it.isNotEmpty() }?.apply {

                toContainer.add(this.trim())
            }
        }
        return toContainer
    }
}


class DiscussionCreateResultToEntityMapper :
    CyberToEntityMapper<CreateDiscussionResult, DiscussionCreationResultEntity> {
    override suspend fun invoke(cyberObject: CreateDiscussionResult): DiscussionCreationResultEntity {
        return when (cyberObject.parent_id?.author?.name.orEmpty().isEmpty()) {

            true -> PostCreationResultEntity(
                DiscussionIdEntity(
                    cyberObject.message_id.author.name,
                    cyberObject.message_id.permlink
                )
            )
            false -> CommentCreationResultEntity(
                DiscussionIdEntity(
                    cyberObject.message_id.author.name,
                    cyberObject.message_id.permlink
                ),
                DiscussionIdEntity(
                    cyberObject.parent_id.author.name,
                    cyberObject.parent_id.permlink
                )
            )
        }
    }
}


class DiscussionUpdateResultToEntityMapper :
    CyberToEntityMapper<UpdateDiscussionResult, UpdatePostResultEntity> {
    override suspend fun invoke(cyberObject: UpdateDiscussionResult): UpdatePostResultEntity {
        return UpdatePostResultEntity(
            DiscussionIdEntity(
                cyberObject.message_id.author.name,
                cyberObject.message_id.permlink
            )
        )
    }
}


class DiscussionDeleteResultToEntityMapper :
    CyberToEntityMapper<DeleteResult, DeleteDiscussionResultEntity> {
    override suspend fun invoke(cyberObject: DeleteResult): DeleteDiscussionResultEntity {
        return DeleteDiscussionResultEntity(
            DiscussionIdEntity(
                cyberObject.message_id.author.name,
                cyberObject.message_id.permlink
            )
        )
    }
}