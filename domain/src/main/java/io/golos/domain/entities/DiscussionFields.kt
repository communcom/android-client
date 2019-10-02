package io.golos.domain.entities

import io.golos.domain.Entity
import java.math.BigInteger
import java.util.*

data class DiscussionCommentsCount(val count: Long) : Entity

data class PostContent(
    val title: String,
    val body: ContentBody,
    val tags: List<TagEntity>
) : Entity

data class CommentContent(val body: ContentBody) : Entity

data class ContentBody(
    val preview: String,
    val full: String,
    val embeds: List<EmbedEntity>,
    val mobilePreview : String
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
    val upCount: Int,
    val downCount: Int
) : Entity

data class TagEntity(val tag: String)
