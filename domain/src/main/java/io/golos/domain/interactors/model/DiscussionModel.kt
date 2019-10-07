package io.golos.domain.interactors.model

import android.os.Parcelable
import io.golos.domain.Model
import io.golos.domain.post.post_dto.PostBlock
import kotlinx.android.parcel.Parcelize
import java.math.BigInteger
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
data class PostModel(
    override val contentId: DiscussionIdModel,
    override val author: DiscussionAuthorModel,
    val community: CommunityModel,
    val content: PostContentModel,
    override val votes: DiscussionVotesModel,
    val comments: DiscussionCommentsCountModel,
    override val payout: DiscussionPayoutModel,
    override val meta: DiscussionMetadataModel,
    override val stats: DiscussionStatsModel
) : DiscussionModel(
    contentId, author, votes,
    payout, meta, stats
)

data class CommentModel(
    override val contentId: DiscussionIdModel,
    override val author: DiscussionAuthorModel,
    val content: CommentContentModel,
    override val votes: DiscussionVotesModel,
    override val payout: DiscussionPayoutModel,
    val parentCommentId: DiscussionIdModel?,
    override val meta: DiscussionMetadataModel,
    override val stats: DiscussionStatsModel
) : DiscussionModel(
    contentId, author, votes, payout, meta, stats
)

sealed class DiscussionModel(
    open val contentId: DiscussionIdModel,
    open val author: DiscussionAuthorModel,
    open val votes: DiscussionVotesModel,
    open val payout: DiscussionPayoutModel,
    open val meta: DiscussionMetadataModel,
    open val stats: DiscussionStatsModel,
    var isActiveUserDiscussion: Boolean = false
) : Model

@Parcelize
data class DiscussionIdModel(
    val userId: String,
    val permlink: String
) : Model, Parcelable

data class DiscussionCommentsCountModel(val count: Long) : Model

data class PostContentModel(
    val body: ContentBodyModel,
    val tags: List<TagModel>
) : Model

data class CommentContentModel(
    val body: ContentBodyModel,
    val commentLevel: Int
) : Model

data class EmbedModel(
    val type: String,
    val title: String,
    val url: String,
    val author: String,
    val provider_name: String,
    val html: String
) : Model

data class ContentBodyModel(
    val postBlock: PostBlock
) : Model


data class DiscussionMetadataModel(val time: Date, val elapsedFormCreation: ElapsedTime) : Model

class DiscussionPayoutModel : Model

data class DiscussionStatsModel(val rShares: BigInteger, val viewsCount: Long) : Model

data class DiscussionVotesModel(
    val hasUpVote: Boolean,
    val hasDownVote: Boolean,
    val upCount: Long,
    val downCount: Long,
    val hasUpVoteProgress: Boolean,
    val hasDownVotingProgress: Boolean,
    val hasVoteCancelProgress: Boolean
) : Model

data class ElapsedTime(val elapsedMinutes: Int, val elapsedHours: Int, val elapsedDays: Int)

data class TagModel(val tag: String)
