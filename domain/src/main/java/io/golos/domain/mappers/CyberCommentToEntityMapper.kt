package io.golos.domain.mappers

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.entities.*
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import javax.inject.Inject

interface CyberCommentToEntityMapper : CommunToEntityMapper<CommentDiscussionRaw, CommentEntity>

class CyberCommentToEntityMapperImpl
@Inject
constructor() : CyberCommentToEntityMapper {

    private val jsonMapper = JsonToDtoMapper()

    override fun map(communObject: CommentDiscussionRaw): CommentEntity {
        return CommentEntity(
            DiscussionIdEntity(
                communObject.contentId.userId,
                Permlink(communObject.contentId.permlink)
            ),
            DiscussionAuthorEntity(
                CyberUser(communObject.author.userId.name ?: "unknown"),
                communObject.author.username ?: "unknown",
                communObject.author.avatarUrl ?: ""
            ),
            CommentContent(
                ContentBody(jsonMapper.map(communObject.content))
            ),
            DiscussionVotes(
                communObject.votes.upCount > 0,
                communObject.votes.downCount > 0,
                communObject.votes.upCount,
                communObject.votes.downCount
            ),
            DiscussionPayout(),
            null,                           // note[AS] it's an Id of a parent comment. Temporary null - it'll be in a future
            DiscussionMetadata(communObject.meta.creationTime),
            DiscussionStats(0.toBigInteger(), 0L)
        )
    }
}