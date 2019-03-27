package io.golos.domain.interactors.model

import io.golos.domain.Model
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
    override val meta: DiscussionMetadataModel
) : DiscussionModel(
    contentId, author, votes,
    payout, meta
)

data class CommentModel(
    override val contentId: DiscussionIdModel,
    override val author: DiscussionAuthorModel,
    val content: CommentContentModel,
    override val votes: DiscussionVotesModel,
    override val payout: DiscussionPayoutModel,
    val parentPostId: DiscussionIdModel,
    val parentCommentId: DiscussionIdModel?,
    override val meta: DiscussionMetadataModel
) : DiscussionModel(
    contentId, author, votes, payout, meta
)

sealed class DiscussionModel(
    open val contentId: DiscussionIdModel,
    open val author: DiscussionAuthorModel,
    open val votes: DiscussionVotesModel,
    open val payout: DiscussionPayoutModel,
    open val meta: DiscussionMetadataModel
) : Model


data class DiscussionIdModel(
    val userId: String,
    val permlink: String,
    val refBlockNum: Long
) : Model

data class DiscussionCommentsCountModel(val count: Long) : Model

data class PostContentModel(val title: String, val body: ContentBodyModel, val metadata: Any) : Model
data class CommentContentModel(val body: ContentBodyModel, val metadata: Any) : Model


data class ContentBodyModel(
    val preview: String?,
    val full: String?
) : Model

data class DiscussionMetadataModel(val time: Date, val elapsedFormCreation: ElapsedTime) : Model
data class DiscussionPayoutModel(val rShares: BigInteger) : Model
data class DiscussionVotesModel(
    val hasUpVote: Boolean,
    val hasDownVote: Boolean,
    val upCount: Int,
    val downCount: Int,
    val hasUpVoteProgress: Boolean,
    val hasDownVotingProgress: Boolean,
    val hasVoteCancelProgress: Boolean
) : Model

data class ElapsedTime(val elapsedMinutes: Int, val elapsedHours: Int, val elapsedDays: Int)
