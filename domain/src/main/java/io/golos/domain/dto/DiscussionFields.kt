package io.golos.domain.dto

import io.golos.domain.Entity
import io.golos.domain.use_cases.post.post_dto.ContentBlock
import java.math.BigInteger
import java.util.*

data class DiscussionCommentsCount(val count: Int) : Entity

data class PostContent(
    val body: ContentBody,
    val tags: List<TagEntity>
) : Entity

data class CommentContent(val body: ContentBody) : Entity

data class ContentBody(
    val postBlock: ContentBlock
) : Entity

data class EmbedEntity(
    val type: String,
    val title: String,
    val url: String,
    val author: String,
    val provider_name: String,
    val html: String
) : Entity

data class DiscussionMetadata(val time: Date) : Entity

class DiscussionPayout : Entity

data class DiscussionStats(val rShares: BigInteger, val viewsCount: Int) : Entity

data class DiscussionVotes(
    val hasUpVote: Boolean,
    val hasDownVote: Boolean,
    val upCount: Long,
    val downCount: Long
) : Entity

data class TagEntity(val tag: String)
