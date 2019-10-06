package io.golos.domain.mappers

import io.golos.commun4j.model.CyberDiscussion
import io.golos.domain.entities.*
import javax.inject.Inject

interface CyberCommentToEntityMapper : CommunToEntityMapper<CyberDiscussion, CommentEntity>

class CyberCommentToEntityMapperImpl
@Inject
constructor() : CyberCommentToEntityMapper {

    override suspend fun map(communObject: CyberDiscussion): CommentEntity {
        return CommentEntity(
            DiscussionIdEntity(
                communObject.contentId.userId,
                communObject.contentId.permlink
            ),
            DiscussionAuthorEntity(
                CyberUser(communObject.author.userId.name ?: "unknown"),
                communObject.author.username ?: "unknown",
                communObject.author.avatarUrl ?: ""
            ),
            CommentContent(
                ContentBody("")
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