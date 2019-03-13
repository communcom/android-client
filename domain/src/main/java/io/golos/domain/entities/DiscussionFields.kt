package io.golos.domain.entities

import java.math.BigInteger
import java.util.*

data class DiscussionCommentsCount(val count: Long)
data class DiscussionContent(val title: String, val body: ContentBody, val metadata: Any)
data class ContentBody(
    val preview: String?,
    val full: String?
)

data class DiscussionMetadata(val time: Date)
data class DiscussionPayout(val rShares: BigInteger)
data class DiscussionVotes(
    val hasUpVote: Boolean,
    val hasDownVote: Boolean
)

data class ParentId(val userId: String, val permlink: String, val refBlockNum: Int)