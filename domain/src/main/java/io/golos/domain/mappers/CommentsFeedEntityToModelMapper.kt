package io.golos.domain.mappers

import io.golos.domain.dto.CommentEntity
import io.golos.domain.dto.DiscussionRelatedEntities
import io.golos.domain.dto.FeedRelatedEntities
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionsFeed
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