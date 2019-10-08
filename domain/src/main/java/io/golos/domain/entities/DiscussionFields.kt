package io.golos.domain.entities

import io.golos.domain.Entity
import io.golos.domain.post.post_dto.PostBlock
import java.math.BigInteger
import java.util.*

data class DiscussionCommentsCount(val count: Long) : Entity

data class PostContent(
    val body: ContentBody,
    val tags: List<TagEntity>
) : Entity

data class CommentContent(val body: ContentBody) : Entity

data class ContentBody(
    val postBlock: PostBlock
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

data class DiscussionStats(val rShares: BigInteger, val viewsCount: Long) : Entity

data class DiscussionVotes(
    val hasUpVote: Boolean,
    val hasDownVote: Boolean,
    val upCount: Long,
    val downCount: Long
) : Entity

data class TagEntity(val tag: String)
