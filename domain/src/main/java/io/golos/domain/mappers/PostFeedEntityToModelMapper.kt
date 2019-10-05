package io.golos.domain.mappers

import io.golos.domain.entities.DiscussionRelatedEntities
import io.golos.domain.entities.FeedRelatedEntities
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.PostModel
import javax.inject.Inject

class PostFeedEntityToModelMapper
@Inject
constructor(private val postMapper: EntityToModelMapper<DiscussionRelatedEntities<PostEntity>, PostModel>) :
    EntityToModelMapper<FeedRelatedEntities<PostEntity>, DiscussionsFeed<PostModel>> {

    override suspend fun invoke(entity: FeedRelatedEntities<PostEntity>): DiscussionsFeed<PostModel> {
        val posts = entity.feed.discussions
        val votes = entity.votes.values.associateBy { it.originalQuery.discussionIdEntity }

        return DiscussionsFeed(posts
            .map { postEntity ->
                DiscussionRelatedEntities(postEntity, votes[postEntity.contentId])
            }
            .map { postRelatedEntities -> postMapper(postRelatedEntities) })
    }
}