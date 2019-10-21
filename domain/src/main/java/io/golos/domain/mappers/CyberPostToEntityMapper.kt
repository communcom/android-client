package io.golos.domain.mappers

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.commun_entities.PostDiscussionRaw
import io.golos.domain.entities.*
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import javax.inject.Inject

interface CyberPostToEntityMapper : CommunToEntityMapper<PostDiscussionRaw, PostEntity>

class CyberPostToEntityMapperImpl
@Inject
constructor() : CyberPostToEntityMapper {

    private val jsonMapper = JsonToDtoMapper()

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun map(communObject: PostDiscussionRaw): PostEntity {
        return PostEntity(
            DiscussionIdEntity(
                communObject.contentId.userId,
                Permlink(communObject.contentId.permlink)
            ),
            DiscussionAuthorEntity(
                CyberUser(communObject.author.userId.name ?: "unknown"),
                communObject.author.username ?: "unknown",
                communObject.author.avatarUrl ?: ""
            ),
            CommunityEntity(
                communObject.community.id,
                communObject.community.name!!,
                communObject.community.avatarUrl
            ),
            PostContent(
                ContentBody(jsonMapper.map(communObject.content)),
                listOf()
            ),
            DiscussionVotes(
                communObject.votes.upCount > 0,
                communObject.votes.downCount > 0,
                communObject.votes.upCount,
                communObject.votes.downCount
            ),
            DiscussionCommentsCount(communObject.childCount),     // note[AS] temporary zero - it'll be in a future
            DiscussionPayout(),
            DiscussionMetadata(communObject.meta.creationTime),
            DiscussionStats(
                0.toBigInteger(),
                0L
            )                  // note[AS] temporary zero - it'll be in a future
        )
    }
}