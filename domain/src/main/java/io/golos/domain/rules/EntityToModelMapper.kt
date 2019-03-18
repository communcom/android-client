package io.golos.domain.rules

import androidx.annotation.WorkerThread
import io.golos.domain.Entity
import io.golos.domain.Model
import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
interface EntityToModelMapper<E : Entity, M : Model> {
    @WorkerThread
    suspend operator fun invoke(entity: E): M
}

class PostEntityToModelMapper : EntityToModelMapper<PostEntity, PostModel> {
    override suspend fun invoke(entity: PostEntity): PostModel {
        return PostModel(
            DiscussionIdModel(entity.contentId.userId, entity.contentId.permlink, entity.contentId.refBlockNum),
            DiscussionAuthorModel(entity.author.userId, entity.author.username),
            CommunityModel(CommunityId(entity.community.id), entity.community.name, entity.community.avatarUrl),
            DiscussionContentModel(
                entity.content.title,
                ContentBodyModel(entity.content.body.preview, entity.content.body.full),
                entity.content.metadata
            ),
            DiscussionVotesModel(entity.votes.hasUpVote, entity.votes.hasDownVote),
            DiscussionCommentsCountModel(entity.comments.count),
            DiscussionPayoutModel(entity.payout.rShares),
            DiscussionMetadataModel(entity.meta.time)
        )
    }
}

class PostFeedEntityToModelMapper(private val postMapper: EntityToModelMapper<PostEntity, PostModel>) :
    EntityToModelMapper<FeedEntity<PostEntity>, PostFeed> {
    override suspend fun invoke(entity: FeedEntity<PostEntity>): PostFeed {
        return PostFeed(entity.discussions.map { postMapper(it) })
    }
}