package io.golos.domain.mappers

import io.golos.domain.commun_entities.Permlink
import io.golos.domain.commun_entities.PostDiscussionRaw
import io.golos.domain.dto.*
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
                communObject.contentId.userId.name,
                Permlink(communObject.contentId.permlink)
            ),
            DiscussionAuthorEntity(
                CyberUser(communObject.author.userId.name ?: "unknown"),
                communObject.author.username ?: "unknown",
                communObject.author.avatarUrl ?: ""
            ),
            CommunityEntity(
                communObject.community.communityId,
                communObject.community.name!!,
                "",
                "",
                "",
                0,
                0,
                false
                //                communObject.community.avatarUrl,
                //                communObject.community.isSubscribed ?: false
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
            DiscussionMetadata(communObject.meta.creationTime,communObject.meta.trxId),
            DiscussionStats(
                0.toBigInteger(),
                0
            ),                  // note[AS] temporary zero - it'll be in a future
            communObject.shareUrl
        )
    }
}