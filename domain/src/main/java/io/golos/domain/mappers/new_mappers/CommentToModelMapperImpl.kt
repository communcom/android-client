package io.golos.domain.mappers.new_mappers

import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.interactors.model.*
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import javax.inject.Inject

interface CommentToModelMapper {
    fun map(entity: CommentDiscussionRaw): CommentModel
}

class CommentToModelMapperImpl
@Inject
constructor() : CommentToModelMapper {
    private val jsonMapper = JsonToDtoMapper()          // Interface and injection!!!

    override fun map(entity: CommentDiscussionRaw): CommentModel {
        val commentLevel = if(entity.parentContentId == null) 0 else 1

        return CommentModel(
            entity.contentId.map(),
            entity.author.map(),
            CommentContentModel(ContentBodyModel(jsonMapper.map(entity.content)), commentLevel),
            entity.votes.map(),
            DiscussionPayoutModel(),
            entity.parentContentId?.map(),
            entity.meta.map(),
            DiscussionStatsModel(0.toBigInteger(), 0L)
        )
    }
}