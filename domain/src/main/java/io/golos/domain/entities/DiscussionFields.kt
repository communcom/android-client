package io.golos.domain.entities

import io.golos.domain.Entity
import java.math.BigInteger
import java.util.*

data class DiscussionCommentsCount(val count: Long):Entity
data class DiscussionContent(val title: String, val body: ContentBody, val metadata: Any):Entity
data class ContentBody(
    val preview: String?,
    val full: String?
):Entity

data class DiscussionMetadata(val time: Date):Entity
data class DiscussionPayout(val rShares: BigInteger):Entity
data class DiscussionVotes(
    val hasUpVote: Boolean,
    val hasDownVote: Boolean
):Entity

data class ParentId(val userId: String, val permlink: String, val refBlockNum: Int):Entity