package io.golos.domain.mappers

import io.golos.commun4j.services.model.*
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@ApplicationScope
class EventsToEntityMapper
@Inject
constructor() :
    CommunToEntityMapper<EventsListDataWithQuery, EventsListEntity> {
    private val cache =
        Collections.synchronizedMap(HashMap<Event, EventEntity>())

    override suspend fun map(communObject: EventsListDataWithQuery): EventsListEntity {
        return EventsListEntity(
            communObject.data.total,
            communObject.data.fresh,
            communObject.query.lastEventId,
            communObject.data.data.map { event ->
                cache.getOrPut(event) {
                    when (event) {
                        is VoteEvent -> VoteEventEntity(
                            event.actor.toEventActor(),
                            event.post?.toEventPost(),
                            event.comment?.toEventComment(),
                            event.community.toEntity(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                        is FlagEvent -> FlagEventEntity(
                            event.actor.toEventActor(),
                            event.post?.toEventPost(),
                            event.comment?.toEventComment(),
                            event.community.toEntity(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                        is TransferEvent -> TransferEventEntity(
                            event.value.toEventValue(),
                            event.actor.toEventActor(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                        is SubscribeEvent -> SubscribeEventEntity(
                            event.community.toEntity(),
                            event.actor.toEventActor(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                        is UnSubscribeEvent -> UnSubscribeEventEntity(
                            event.community.toEntity(),
                            event.actor.toEventActor(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                        is ReplyEvent -> ReplyEventEntity(
                            event.comment.toEventComment(),
                            event.post?.toEventPost(),
                            event.parentComment?.toEventComment(),
                            event.community.toEntity(),
                            event.actor.toEventActor(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                        is MentionEvent -> MentionEventEntity(
                            event.comment?.toEventComment(),
                            event.post?.toEventPost(),
                            event.parentComment?.toEventComment(),
                            event.community.toEntity(),
                            event.actor.toEventActor(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                        is RepostEvent -> RepostEventEntity(
                            event.post.toEventPost(),
                            event.comment?.toEventComment(),
                            event.community.toEntity(),
                            event.actor.toEventActor(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                        is AwardEvent -> AwardEventEntity(
                            event.payout.toEventValue(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                        is CuratorAwardEvent -> CuratorAwardEventEntity(
                            event.post?.toEventPost(),
                            event.comment?.toEventComment(),
                            event.payout.toEventValue(),
                            event.community.toEntity(),
                            event.actor.toEventActor(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
//                        is MessageEvent -> MessageEventEntity(
//                            event.actor.toEventActor(),
//                            event._id,
//                            event.fresh,
//                            event.timestamp
//                        )
                        is WitnessVoteEvent -> WitnessVoteEventEntity(
                            event.actor.toEventActor(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                        is WitnessCancelVoteEvent -> WitnessCancelVoteEventEntity(
                            event.actor.toEventActor(),
                            event._id,
                            event.fresh,
                            event.timestamp
                        )
                    }
                }
            }
        )
    }

    private fun Actor.toEventActor() =
        EventActorEntity(this.userId, this.avatarUrl)
    private fun Post.toEventPost() = EventPostEntity(
        DiscussionIdEntity.fromCyber(this.contentId),
        this.title ?: ""
    )
    private fun Comment.toEventComment() = EventCommentEntity(
        DiscussionIdEntity.fromCyber(this.contentId),
        this.body
    )
    private fun Value.toEventValue() =
        EventValueEntity(this.amount, this.currency)
    private fun CyberCommunity.toEntity() =
        CommunityEntity.fromCyber(this)
}