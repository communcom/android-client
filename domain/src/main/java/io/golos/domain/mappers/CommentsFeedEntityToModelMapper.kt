package io.golos.domain.mappers

import io.golos.domain.entities.CommentEntity
import io.golos.domain.entities.DiscussionRelatedEntities
import io.golos.domain.entities.FeedRelatedEntities
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionsFeed
import javax.inject.Inject

interface CommentsFeedEntityToModelMapper : EntityToModelMapper<FeedRelatedEntities<CommentEntity>, DiscussionsFeed<CommentModel>>

class CommentsFeedEntityToModelMapperImpl
@Inject
constructor(
    private val commentsMapper: CommentEntityToModelMapper
) : CommentsFeedEntityToModelMapper {

    override fun map(entity: FeedRelatedEntities<CommentEntity>): DiscussionsFeed<CommentModel> {
        val comments = entity.feed.discussions
        val votes = entity.votes.values.associateBy { it.originalQuery.discussionIdEntity }

        return DiscussionsFeed(comments
            .map { commentEntity ->
                DiscussionRelatedEntities(commentEntity, votes[commentEntity.contentId])

            }
            .map { postRelatedEntities -> commentsMapper.map(postRelatedEntities) })
    }
}