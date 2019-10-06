package io.golos.domain.mappers

import io.golos.domain.entities.DiscussionRelatedEntities
import io.golos.domain.entities.FeedRelatedEntities
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.PostModel
import javax.inject.Inject

interface PostFeedEntityToModelMapper : EntityToModelMapper<FeedRelatedEntities<PostEntity>, DiscussionsFeed<PostModel>>

class PostFeedEntityToModelMapperImpl
@Inject
constructor(
    private val postMapper: PostEntitiesToModelMapper
) : PostFeedEntityToModelMapper {

    override suspend fun map(entity: FeedRelatedEntities<PostEntity>): DiscussionsFeed<PostModel> {
        val posts = entity.feed.discussions
        val votes = entity.votes.values.associateBy { it.originalQuery.discussionIdEntity }

        return DiscussionsFeed(posts
            .map { postEntity ->
                DiscussionRelatedEntities(postEntity, votes[postEntity.contentId])
            }
            .map { postRelatedEntities -> postMapper.map(postRelatedEntities) })
    }
}