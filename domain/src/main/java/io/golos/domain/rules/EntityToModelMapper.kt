package io.golos.domain.rules

import androidx.annotation.WorkerThread
import io.golos.domain.Entity
import io.golos.domain.HtmlToSpannableTransformer
import io.golos.domain.Model
import io.golos.domain.asElapsedTime
import io.golos.domain.entities.*
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
interface EntityToModelMapper<E : Entity, M : Model> {
    @WorkerThread
    suspend operator fun invoke(entity: E): M
}

class PostEntityEntitiesToModelMapper(private val htmlToSpannableTransformer: HtmlToSpannableTransformer) :

    EntityToModelMapper<DiscussionRelatedEntities<PostEntity>, PostModel> {
    private val cashedValues = Collections.synchronizedMap(HashMap<DiscussionRelatedEntities<PostEntity>, PostModel>())
    private val cashedSpans = Collections.synchronizedMap(HashMap<String, CharSequence>())

    override suspend fun invoke(entity: DiscussionRelatedEntities<PostEntity>): PostModel {
        val post = entity.discussionEntity

        val voteEntity = entity.voteStateEntity

        val out = cashedValues.getOrPut(entity) {
            PostModel(
                DiscussionIdModel(post.contentId.userId, post.contentId.permlink),
                DiscussionAuthorModel(post.author.userId, post.author.username, post.author.avatarUrl),
                CommunityModel(CommunityId(post.community.id), post.community.name, post.community.avatarUrl),
                PostContentModel(
                    post.content.title,
                    ContentBodyModel(
                        post.content.body.full.map { rowEntity ->
                            rowEntity.toContentRowModel(htmlToSpannableTransformer, cashedSpans)
                        },
                        post.content.body.embeds.map { it.toEmbedModel() },
                        post.content.body.mobilePreview.map { rowEntity ->
                            rowEntity.toContentRowModel(htmlToSpannableTransformer, cashedSpans)
                        }
                    ),
                    post.content.tags.map { it.toModel() }
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
        return out
    }
}

class CommentEntityToModelMapper(private val htmlToSpannableTransformer: HtmlToSpannableTransformer) :
    EntityToModelMapper<DiscussionRelatedEntities<CommentEntity>, CommentModel> {


    private val cashedValues =
        Collections.synchronizedMap(HashMap<DiscussionRelatedEntities<CommentEntity>, CommentModel>())
    private val cashedSpans = Collections.synchronizedMap(HashMap<String, CharSequence>())


    override suspend fun invoke(entity: DiscussionRelatedEntities<CommentEntity>): CommentModel {
        val comment = entity.discussionEntity

        val voteEntity = entity.voteStateEntity

        val out = cashedValues.getOrPut(entity) {
            CommentModel(
                DiscussionIdModel(comment.contentId.userId, comment.contentId.permlink),
                DiscussionAuthorModel(comment.author.userId, comment.author.username, comment.author.avatarUrl),
                CommentContentModel(
                    ContentBodyModel(
                        comment.content.body.full.map { rowEntity ->
                            rowEntity.toContentRowModel(htmlToSpannableTransformer, cashedSpans)
                        },
                        comment.content.body.embeds.map { it.toEmbedModel() },
                        comment.content.body.mobilePreview.map { rowEntity ->
                            rowEntity.toContentRowModel(htmlToSpannableTransformer, cashedSpans)
                        }),
                    if (comment.parentCommentId != null) 1 else 0
                ),
                DiscussionVotesModel(
                    comment.votes.hasUpVote,
                    comment.votes.hasDownVote,
                    comment.votes.upCount,
                    comment.votes.downCount,
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power > 0),
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power < 0),
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power == 0.toShort())
                ),
                DiscussionPayoutModel(comment.payout.rShares),
                DiscussionIdModel(
                    comment.parentPostId.userId,
                    comment.parentPostId.permlink
                ),
                comment.parentCommentId?.let {
                    DiscussionIdModel(it.userId, it.permlink)
                },
                DiscussionMetadataModel(
                    comment.meta.time, comment.meta.time.asElapsedTime()
                )
            )
        }
        return out
    }
}

private fun ContentRowEntity.toContentRowModel(
    transformer: HtmlToSpannableTransformer,
    cache: MutableMap<String, CharSequence>
): ContentRowModel {
    return when (this) {
        is ImageRowEntity -> ImageRowModel(src)
        is TextRowEntity -> TextRowModel(cache.getOrPut(text) { transformer.transform(text) })
    }
}

private fun EmbedEntity.toEmbedModel() = EmbedModel(type, title, url, author, provider_name, html)

private fun TagEntity.toModel() = TagModel(this.tag)


class PostFeedEntityToModelMapper(private val postMapper: EntityToModelMapper<DiscussionRelatedEntities<PostEntity>, PostModel>) :
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


class CommentsFeedEntityToModelMapper(private val commentsMapper: EntityToModelMapper<DiscussionRelatedEntities<CommentEntity>, CommentModel>) :
    EntityToModelMapper<FeedRelatedEntities<CommentEntity>, DiscussionsFeed<CommentModel>> {

    override suspend fun invoke(entity: FeedRelatedEntities<CommentEntity>): DiscussionsFeed<CommentModel> {
        val comments = entity.feed.discussions
        val votes = entity.votes.values.associateBy { it.originalQuery.discussionIdEntity }

        return DiscussionsFeed(comments
            .map { commentEntity ->
                DiscussionRelatedEntities(commentEntity, votes[commentEntity.contentId])

            }
            .map { postRelatedEntities -> commentsMapper(postRelatedEntities) })
    }
}


class VoteRequestEntityToModelMapper : EntityToModelMapper<VoteRequestEntity, VoteRequestModel> {
    private val cash = Collections.synchronizedMap(HashMap<VoteRequestEntity, VoteRequestModel>())

    override suspend fun invoke(entity: VoteRequestEntity): VoteRequestModel {
        return cash.getOrPut(entity) {
            when (entity) {
                is VoteRequestEntity.VoteForAPostRequestEntity -> VoteRequestModel.VoteForPostRequest(
                    entity.power,
                    DiscussionIdModel(
                        entity.discussionIdEntity.userId,
                        entity.discussionIdEntity.permlink
                    )
                )
                is VoteRequestEntity.VoteForACommentRequestEntity -> VoteRequestModel.VoteForComentRequest(
                    entity.power,
                    DiscussionIdModel(
                        entity.discussionIdEntity.userId,
                        entity.discussionIdEntity.permlink
                    )
                )
            }
        }
    }
}

class UserMetadataEntityToModelMapper : EntityToModelMapper<UserMetadataEntity, UserMetadataModel> {
    override suspend fun invoke(entity: UserMetadataEntity): UserMetadataModel {
        return with(entity) {
            UserMetadataModel(
                UserPersonalDataModel(
                    personal.avatarUrl, personal.coverUrl, personal.biography,
                    ContactsModel(
                        personal.contacts?.facebook, personal.contacts?.telegram, personal.contacts?.whatsApp,
                        personal.contacts?.weChat
                    )

                ), UserSubscriptionsModel(subscriptions.usersCount, subscriptions.communitiesCount)
                , UserStatsModel(stats.postsCount, stats.commentsCount)
                , userId
                , username
                , SubscribersModel(subscribers.usersCount, subscribers.communitiesCount)
                , createdAt
                , isSubscribed
            )
        }
    }
}

class EventEntityToModelMapper : EntityToModelMapper<EventsListEntity, EventsListModel> {
    private val cache = Collections.synchronizedMap(HashMap<EventEntity, EventModel>())

    override suspend fun invoke(entity: EventsListEntity): EventsListModel {
        return EventsListModel(entity.data.map { event ->
            cache.getOrPut(event) {
                when (event) {
                    is VoteEventEntity -> VoteEventModel(
                        event.actor.toModelActor(),
                        event.post?.toModelPost(),
                        event.comment?.toModelComment(),
                        event.community.toModel(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is FlagEventEntity -> FlagEventModel(
                        event.actor.toModelActor(),
                        event.post?.toModelPost(),
                        event.comment?.toModelComment(),
                        event.community.toModel(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is TransferEventEntity -> TransferEventModel(
                        event.value.toModelValue(),
                        event.actor.toModelActor(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is SubscribeEventEntity -> SubscribeEventModel(
                        event.community.toModel(),
                        event.actor.toModelActor(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is UnSubscribeEventEntity -> UnSubscribeEventModel(
                        event.community.toModel(),
                        event.actor.toModelActor(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is ReplyEventEntity -> ReplyEventModel(
                        event.comment.toModelComment(),
                        event.parentPost?.toModelPost(),
                        event.parentComment?.toModelComment(),
                        event.community.toModel(),
                        event.actor.toModelActor(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is MentionEventEntity -> MentionEventModel(
                        event.comment?.toModelComment(),
                        event.parentPost?.toModelPost(),
                        event.parentComment?.toModelComment(),
                        event.community.toModel(),
                        event.actor.toModelActor(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is RepostEventEntity -> RepostEventModel(
                        event.post.toModelPost(),
                        event.comment?.toModelComment(),
                        event.community.toModel(),
                        event.actor.toModelActor(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is AwardEventEntity -> AwardEventModel(
                        event.payout.toModelValue(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is CuratorAwardEventEntity -> CuratorAwardEventModel(
                        event.post?.toModelPost(),
                        event.comment?.toModelComment(),
                        event.payout.toModelValue(),
                        event.community.toModel(),
                        event.actor.toModelActor(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is WitnessVoteEventEntity -> WitnessVoteEventModel(
                        event.actor.toModelActor(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                    is WitnessCancelVoteEventEntity -> WitnessCancelVoteEventModel(
                        event.actor.toModelActor(),
                        event.eventId,
                        event.isFresh,
                        event.timestamp
                    )
                }
            }
        })
    }

    private fun EventActorEntity.toModelActor() = EventActorModel(this.id, this.avatarUrl)
    private fun EventPostEntity.toModelPost() = EventPostModel(
        DiscussionIdModel(
            this.contentId.userId,
            this.contentId.permlink
        ), this.title
    )

    private fun EventCommentEntity.toModelComment() =
        EventCommentModel(
            DiscussionIdModel(
                this.contentId.userId, this.contentId.permlink
            ), this.body
        )

    private fun EventValueEntity.toModelValue() = EventValueModel(this.amount, this.currency)
    private fun CommunityEntity.toModel() = CommunityModel(CommunityId(this.id), this.name, this.avatarUrl)

}


