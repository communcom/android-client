package io.golos.domain.mappers

import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.dependency_injection.scopes.UIScope
import io.golos.domain.dto.*
import io.golos.domain.use_cases.model.CommunityModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.requestmodel.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

interface EventEntityToModelMapper : EntityToModelMapper<EventsListEntity, EventsListModel>

@UIScope
class EventEntityToModelMapperImpl
@Inject
constructor() : EventEntityToModelMapper {
    private val cache = Collections.synchronizedMap(HashMap<EventEntity, EventModel>())

    override fun map(entity: EventsListEntity): EventsListModel {
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

    private fun EventActorEntity.toModelActor() =
        EventActorModel(this.id, this.avatarUrl)
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

    private fun EventValueEntity.toModelValue() =
        EventValueModel(this.amount, this.currency)
    private fun CommunityEntity.toModel() = CommunityModel(
        CommunityId(this.id),
        this.name,
        this.avatarUrl
    )

}