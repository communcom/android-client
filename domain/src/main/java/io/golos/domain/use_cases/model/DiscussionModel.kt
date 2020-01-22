package io.golos.domain.use_cases.model

import io.golos.domain.Model
import io.golos.domain.use_cases.post.post_dto.ContentBlock
import java.math.BigInteger
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
@Deprecated("Not need use, use Domain model")
data class PostModel constructor(
    override val contentId: DiscussionIdModel,
    override val author: DiscussionAuthorModel,
    val community: CommunityModel,
    val content: PostContentModel,
    override val votes: DiscussionVotesModel,
    val comments: DiscussionCommentsCountModel,
    override val payout: DiscussionPayoutModel,
    override val meta: DiscussionMetadataModel,
    override val stats: DiscussionStatsModel,
    val shareUrl: String?
) : DiscussionModel(
    contentId, author, votes,
    payout, meta, stats
)

@Deprecated("Not need use, use Domain model")
data class CommentModel(
    override val contentId: DiscussionIdModel,
    override val author: DiscussionAuthorModel,
    val content: CommentContentModel,
    val body: ContentBlock?,
    override val votes: DiscussionVotesModel,
    override val payout: DiscussionPayoutModel,
    val parentId: DiscussionIdModel?,               // Comment of post
    override val meta: DiscussionMetadataModel,
    override val stats: DiscussionStatsModel,
    val childTotal: Long,
    val child: List<CommentModel>,
    val commentLevel: Int,               // 0 or 1
    val isDeleted: Boolean = false,
    val isMyComment: Boolean = false
) : DiscussionModel(
    contentId, author, votes, payout, meta, stats
)
@Deprecated("Not need use, use Domain model")
sealed class DiscussionModel(
    open val contentId: DiscussionIdModel,
    open val author: DiscussionAuthorModel,
    open val votes: DiscussionVotesModel,
    open val payout: DiscussionPayoutModel,
    open val meta: DiscussionMetadataModel,
    open val stats: DiscussionStatsModel,
    var isActiveUserDiscussion: Boolean = false
) : Model

@Deprecated("Not need use, use Domain model")
data class DiscussionCommentsCountModel(val count: Int) : Model

@Deprecated("Not need use, use Domain model")
data class PostContentModel(
    val body: ContentBodyModel,
    val tags: List<TagModel>
) : Model

@Deprecated("Not need use, use Domain model")
data class CommentContentModel(
    val body: ContentBodyModel,
    val commentLevel: Int               // 0 or 1
) : Model

@Deprecated("Not need use, use Domain model")
data class EmbedModel(
    val type: String,
    val title: String,
    val url: String,
    val author: String,
    val provider_name: String,
    val html: String
) : Model

@Deprecated("Not need use, use Domain model")
data class ContentBodyModel(
    val postBlock: ContentBlock
) : Model


@Deprecated("Not need use, use Domain model")
data class DiscussionMetadataModel(val time: Date, val elapsedFormCreation: ElapsedTime) : Model

@Deprecated("Not need use, use Domain model")
class DiscussionPayoutModel : Model

@Deprecated("Not need use, use Domain model")
data class DiscussionStatsModel(val rShares: BigInteger, val viewsCount: Int) : Model

@Deprecated("Not need use, use Domain model")
data class DiscussionVotesModel(
    val hasUpVote: Boolean,
    val hasDownVote: Boolean,
    val upCount: Long,
    val downCount: Long
) : Model

@Deprecated("Not need use, use Domain model")
data class ElapsedTime(val elapsedMinutes: Int, val elapsedHours: Int, val elapsedDays: Int)

@Deprecated("Not need use, use Domain model")
data class TagModel(val tag: String)
