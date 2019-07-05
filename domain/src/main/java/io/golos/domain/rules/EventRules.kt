package io.golos.domain.rules

import io.golos.cyber4j.services.model.*
import io.golos.domain.entities.*
import io.golos.domain.requestmodel.EventsFeedUpdateRequest
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-24.
 */
class EventsToEntityMapper : CyberToEntityMapper<EventsListDataWithQuery, EventsListEntity> {
    private val cache = Collections.synchronizedMap(HashMap<Event, EventEntity>())

    override suspend fun invoke(cyberObject: EventsListDataWithQuery): EventsListEntity {
        return EventsListEntity(
            cyberObject.data.total,
            cyberObject.data.fresh,
            cyberObject.query.lastEventId,
            cyberObject.data.data.map { event ->
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

    private fun Actor.toEventActor() = EventActorEntity(this.id, this.avatarUrl)
    private fun Post.toEventPost() = EventPostEntity(DiscussionIdEntity.fromCyber(this.contentId), this.title ?: "")
    private fun Comment.toEventComment() = EventCommentEntity(DiscussionIdEntity.fromCyber(this.contentId), this.body)
    private fun Value.toEventValue() = EventValueEntity(this.amount, this.currency)
    private fun CyberCommunity.toEntity() = CommunityEntity.fromCyber(this)
}

class EventsEntityMerger : EntityMerger<EventsListEntity> {
    override fun invoke(new: EventsListEntity, old: EventsListEntity): EventsListEntity {
        if (old.isEmpty()) return new
        if (new.queryLastItemId == null) return new
        return EventsListEntity(new.total, new.freshCount, new.queryLastItemId, old.data + new.data)
    }
}

class EventsApprover : RequestApprover<EventsFeedUpdateRequest> {
    override fun approve(param: EventsFeedUpdateRequest): Boolean {
        return true
    }
}