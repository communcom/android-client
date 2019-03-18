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
    override val community: CommunityModel,
    override val content: DiscussionContentModel,
    override val votes: DiscussionVotesModel,
    override val comments: DiscussionCommentsCountModel,
    override val payout: DiscussionPayoutModel,
    override val meta: DiscussionMetadataModel
) : DiscussionModel(
    contentId, author, community, content,
    votes, comments, payout, null, null, meta
)

data class CommentModel(
    override val contentId: DiscussionIdModel,
    override val author: DiscussionAuthorModel,
    override val community: CommunityModel,
    override val content: DiscussionContentModel,
    override val votes: DiscussionVotesModel,
    override val comments: DiscussionCommentsCountModel,
    override val payout: DiscussionPayoutModel,
    override val postId: ParentIdModel?,
    override val parentCommentId: ParentIdModel?,
    override val meta: DiscussionMetadataModel
) : DiscussionModel(
    contentId, author, community, content,
    votes, comments, payout, postId, parentCommentId, meta
)

sealed class DiscussionModel(
    open val contentId: DiscussionIdModel,
    open val author: DiscussionAuthorModel,
    open val community: CommunityModel,
    open val content: DiscussionContentModel,
    open val votes: DiscussionVotesModel,
    open val comments: DiscussionCommentsCountModel,
    open val payout: DiscussionPayoutModel,
    open val postId: ParentIdModel?,
    open val parentCommentId: ParentIdModel?,
    open val meta: DiscussionMetadataModel
) : Model


data class DiscussionIdModel(
    val userId: String,
    val permlink: String,
    val refBlockNum: Long
) : Model

data class DiscussionCommentsCountModel(val count: Long) : Model
data class DiscussionContentModel(val title: String, val body: ContentBodyModel, val metadata: Any) : Model
data class ContentBodyModel(
    val preview: String?,
    val full: String?
) : Model

data class DiscussionMetadataModel(val time: Date) : Model
data class DiscussionPayoutModel(val rShares: BigInteger) : Model
data class DiscussionVotesModel(
    val hasUpVote: Boolean,
    val hasDownVote: Boolean
) : Model

data class ParentIdModel(val userId: String, val permlink: String, val refBlockNum: Int) : Model