package io.golos.domain.rules

import androidx.annotation.WorkerThread
import io.golos.domain.Entity
import io.golos.domain.Model
import io.golos.domain.asElapsedTime
import io.golos.domain.entities.FeedRelatedEntities
import io.golos.domain.entities.PostEntity
import io.golos.domain.entities.PostRelatedEntities
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.model.*
import io.golos.domain.model.QueryResult
import io.golos.domain.model.VoteRequestModel

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
interface EntityToModelMapper<E : Entity, M : Model> {
    @WorkerThread
    suspend operator fun invoke(entity: E): M
}

class PostEntityEntitiesToModelMapper() :
    EntityToModelMapper<PostRelatedEntities, PostModel> {
    private val cashedValues = HashMap<PostRelatedEntities, PostModel>()

    override suspend fun invoke(entity: PostRelatedEntities): PostModel {
        val post = entity.postEntity
        val voteEntity = entity.voteStateEntity

        return cashedValues.getOrPut(entity) {
            PostModel(
                DiscussionIdModel(post.contentId.userId, post.contentId.permlink, post.contentId.refBlockNum),
                DiscussionAuthorModel(post.author.userId, post.author.username),
                CommunityModel(CommunityId(post.community.id), post.community.name, post.community.avatarUrl),
                DiscussionContentModel(
                    post.content.title,
                    ContentBodyModel(post.content.body.preview, post.content.body.full),
                    post.content.metadata
                ),
                DiscussionVotesModel(
                    post.votes.hasUpVote,
                    post.votes.hasDownVote,
                    post.votes.upCount,
                    post.votes.downCount,
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power > 0),
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power < 0),
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power == 0.toShort())
                ),
                DiscussionCommentsCountModel(post.comments.count),
                DiscussionPayoutModel(post.payout.rShares),
                DiscussionMetadataModel(post.meta.time, post.meta.time.asElapsedTime())
            )
        }
    }
}


class PostEntityToModelMapper : EntityToModelMapper<PostEntity, PostModel> {
    private val cashedValues = HashMap<PostEntity, PostModel>()

    override suspend fun invoke(entity: PostEntity): PostModel {
        return cashedValues.getOrPut(entity) {
            PostModel(
                DiscussionIdModel(entity.contentId.userId, entity.contentId.permlink, entity.contentId.refBlockNum),
                DiscussionAuthorModel(entity.author.userId, entity.author.username),
                CommunityModel(CommunityId(entity.community.id), entity.community.name, entity.community.avatarUrl),
                DiscussionContentModel(
                    entity.content.title,
                    ContentBodyModel(entity.content.body.preview, entity.content.body.full),
                    entity.content.metadata
                ),
                DiscussionVotesModel(
                    entity.votes.hasUpVote,
                    entity.votes.hasDownVote,
                    entity.votes.upCount,
                    entity.votes.downCount,
                    false, false, false
                ),
                DiscussionCommentsCountModel(entity.comments.count),
                DiscussionPayoutModel(entity.payout.rShares),
                DiscussionMetadataModel(entity.meta.time, ElapsedTime(0, 0, 0))
            )
        }
    }
}

class PostFeedEntityToModelMapper(private val postMapper: EntityToModelMapper<PostRelatedEntities, PostModel>) :
    EntityToModelMapper<FeedRelatedEntities, PostFeed> {

    private val cash = HashMap<PostEntity, PostRelatedEntities>()

    override suspend fun invoke(entity: FeedRelatedEntities): PostFeed {
        val posts = entity.feed.discussions
        val votes = entity.votes.values.associateBy { it.originalQuery.discussionIdEntity }

        return PostFeed(posts
            .map { postEntity ->
                cash.getOrPut(postEntity) {
                    PostRelatedEntities(postEntity, null)
                }
            }
            .onEach { postRelatedEntities ->
                postRelatedEntities.voteStateEntity = votes[postRelatedEntities.postEntity.contentId]
            }
            .map { postRelatedEntities -> postMapper(postRelatedEntities) })
    }
}

class VoteRequestEntityToModelMapper : EntityToModelMapper<VoteRequestEntity, VoteRequestModel> {
    private val cash = HashMap<VoteRequestEntity, VoteRequestModel>()

    override suspend fun invoke(entity: VoteRequestEntity): VoteRequestModel {
        return cash.getOrPut(entity) {
            when (entity) {
                is VoteRequestEntity.VoteForAPostRequestEntity -> VoteRequestModel.VoteForPostRequest(
                    entity.power,
                    DiscussionIdModel(
                        entity.discussionIdEntity.userId,
                        entity.discussionIdEntity.permlink,
                        entity.discussionIdEntity.refBlockNum
                    )
                )
                is VoteRequestEntity.VoteForACommentRequestEntity -> VoteRequestModel.VoteForComentRequest(
                    entity.power,
                    DiscussionIdModel(
                        entity.discussionIdEntity.userId,
                        entity.discussionIdEntity.permlink,
                        entity.discussionIdEntity.refBlockNum
                    )
                )
            }
        }
    }
}


