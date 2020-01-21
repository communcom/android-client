package io.golos.domain.mappers.new_mappers

import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.CyberUser
import io.golos.domain.extensions.asElapsedTime
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.domain.use_cases.model.*
import javax.inject.Inject

interface CommentToModelMapper {
    fun map(entity: CommentDiscussionRaw, commentLevel: Int): CommentModel

    fun map(commentDomain: CommentDomain): CommentModel
}

class CommentToModelMapperImpl
@Inject
constructor() : CommentToModelMapper {

    override fun map(commentDomain: CommentDomain): CommentModel {
        val author = commentDomain.author
        val votes = commentDomain.votes
        val parentContentId = commentDomain.parent.comment?.let {
            DiscussionIdModel(it.userId, Permlink(it.permlink))
        }

        return CommentModel(
            DiscussionIdModel(commentDomain.contentId.userId, Permlink(commentDomain.contentId.permlink)),
            DiscussionAuthorModel(CyberUser(author.userId), author.username ?: "", author.avatarUrl),
            CommentContentModel(ContentBodyModel(commentDomain.body!!), commentDomain.commentLevel),
            commentDomain.body,
            DiscussionVotesModel(votes.hasUpVote, votes.hasDownVote, votes.upCount, votes.downCount),
            DiscussionPayoutModel(),
            parentContentId,
            DiscussionMetadataModel(commentDomain.meta.creationTime, commentDomain.meta.creationTime.asElapsedTime()),
            DiscussionStatsModel(0.toBigInteger(), 0),
            commentDomain.childCommentsCount.toLong(),
            listOf(),
            commentDomain.commentLevel,
            commentDomain.isDeleted
        )
    }

    private val jsonMapper = JsonToDtoMapper()          // Interface and injection!!!

    override fun map(entity: CommentDiscussionRaw, commentLevel: Int): CommentModel {
        return CommentModel(
            entity.contentId.map(),
            entity.author.map(),
            CommentContentModel(ContentBodyModel(jsonMapper.map(entity.content)), commentLevel),
            jsonMapper.map(entity.content),
            entity.votes.map(),
            DiscussionPayoutModel(),
            entity.parentContentId?.map(),
            entity.meta.map(),
            DiscussionStatsModel(0.toBigInteger(), 0),
            entity.childTotal,
            entity.child.map { this.map(it, commentLevel+1) },
            commentLevel
        )
    }
}