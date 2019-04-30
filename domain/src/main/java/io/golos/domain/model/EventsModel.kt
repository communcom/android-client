package io.golos.domain.model

import io.golos.cyber4j.model.CyberName
import io.golos.domain.Model
import io.golos.domain.interactors.model.CommunityModel
import io.golos.domain.interactors.model.DiscussionIdModel
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-24.
 */
data class EventsListModel(
    val data: List<EventModel>
) : List<EventModel> by data, Model

interface IdentifiebleEvent {
    val eventId: String
}

sealed class EventModel : Model, IdentifiebleEvent

data class VoteEventModel(
    val actor: EventActorModel,
    val post: EventPostModel?,
    val comment: EventCommentModel?,
    val community: CommunityModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class FlagEventModel(
    val actor: EventActorModel,
    val post: EventPostModel?,
    val comment: EventCommentModel?,
    val community: CommunityModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class TransferEventModel(
    val value: EventValueModel,
    val actor: EventActorModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class SubscribeEventModel(
    val community: CommunityModel,
    val actor: EventActorModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class UnSubscribeEventModel(
    val community: CommunityModel,
    val actor: EventActorModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class ReplyEventModel(
    val comment: EventCommentModel,
    val parentPost: EventPostModel?,
    val parentComment: EventCommentModel?,
    val community: CommunityModel,
    val refBlockNum: Long,
    val actor: EventActorModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class MentionEventModel(
    val comment: EventCommentModel,
    val parentPost: EventPostModel?,
    val parentComment: EventCommentModel?,
    val community: CommunityModel,
    val refBlockNum: Long,
    val actor: EventActorModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class RepostEventModel(
    val post: EventPostModel,
    val comment: EventCommentModel?,
    val community: CommunityModel,
    val refBlockNum: Long,
    val actor: EventActorModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class AwardEventModel(
    val payout: EventValueModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class CuratorAwardEventModel(
    val post: EventPostModel?,
    val comment: EventCommentModel?,
    val payout: EventValueModel,
    val community: CommunityModel,
    val actor: EventActorModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class MessageEventModel(
    val actor: EventActorModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class WitnessVoteEventModel(
    val actor: EventActorModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()

data class WitnessCancelVoteEventModel(
    val actor: EventActorModel,
    override val eventId: String,
    val isFresh: Boolean,
    val timestamp: Date
) : EventModel()


data class EventValueModel(val amount: Double, val currency: String) : Model

data class EventActorModel(val id: CyberName, val avatarUrl: String?) : io.golos.domain.Model

data class EventPostModel(val contentId: DiscussionIdModel, val title: String) : Model

data class EventCommentModel(val contentId: DiscussionIdModel, val body: String) : Model
