package io.golos.domain.mappers

import io.golos.domain.dto.DiscussionRelatedEntities
import io.golos.domain.dto.FeedRelatedEntities
import io.golos.domain.dto.PostEntity
import io.golos.domain.use_cases.model.DiscussionsFeed
import io.golos.domain.use_cases.model.PostModel
import javax.inject.Inject

interface PostFeedEntityToModelMapper : EntityToModelMapper<FeedRelatedEntities<PostEntity>, DiscussionsFeed<PostModel>>

class PostFeedEntityToModelMapperImpl
@Inject
constructor(
    private val postMapper: PostEntitiesToModelMapper
) : PostFeedEntityToModelMapper {

    override fun map(entity: FeedRelatedEntities<PostEntity>): DiscussionsFeed<PostModel> {
        val posts = entity.feed.discussions
        val votes = entity.votes.values.associateBy { it.originalQuery.discussionIdEntity }

        return DiscussionsFeed(posts
            .map { postEntity ->
                DiscussionRelatedEntities(postEntity, votes[postEntity.contentId])
            }
            .map { postRelatedEntities -> postMapper.map(postRelatedEntities) })
    }
}