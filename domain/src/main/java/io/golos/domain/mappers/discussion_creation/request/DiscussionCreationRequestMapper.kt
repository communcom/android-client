package io.golos.domain.mappers.discussion_creation.request

import io.golos.commun4j.model.DiscussionCreateMetadata
import io.golos.commun4j.model.EmbedmentsUrl
import io.golos.commun4j.model.Tag
import io.golos.commun4j.utils.toCyberName
import io.golos.domain.Regexps
import io.golos.domain.mappers.EntityToCommunMapper
import io.golos.domain.requestmodel.*
import javax.inject.Inject

class DiscussionCreationRequestMapper
@Inject
constructor() : EntityToCommunMapper<DiscussionCreationRequestEntity, DiscussionCreateRequest> {

    override suspend fun map(entity: DiscussionCreationRequestEntity): DiscussionCreateRequest {
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
                    DiscussionCreateMetadata((links + entity.images).map { EmbedmentsUrl(it) }),
                    emptyList(),
                    false,
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
                    DiscussionCreateMetadata(links.map { EmbedmentsUrl(it) }),
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
                    DiscussionCreateMetadata((links + entity.images).map { EmbedmentsUrl(it) })
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
