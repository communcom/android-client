package io.golos.domain.mappers.new_mappers

import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.interactors.model.*
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import javax.inject.Inject

interface CommentToModelMapper {
    fun map(entity: CommentDiscussionRaw, commentLevel: Int): CommentModel
}

class CommentToModelMapperImpl
@Inject
constructor() : CommentToModelMapper {
    private val jsonMapper = JsonToDtoMapper()          // Interface and injection!!!

    override fun map(entity: CommentDiscussionRaw, commentLevel: Int): CommentModel {
        return CommentModel(
            entity.contentId.map(),
            entity.author.map(),
            CommentContentModel(ContentBodyModel(jsonMapper.map(entity.content)), commentLevel),
            entity.votes.map(),
            DiscussionPayoutModel(),
            entity.parentContentId?.map(),
            entity.meta.map(),
            DiscussionStatsModel(0.toBigInteger(), 0L),
            entity.childTotal,
            entity.child.map { this.map(it, commentLevel+1) }
        )
    }
}