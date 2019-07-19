package io.golos.domain.entities

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */

class PostEntity(
    contentId: DiscussionIdEntity,
    author: DiscussionAuthorEntity,
    val community: CommunityEntity,
    val content: PostContent,
    votes: DiscussionVotes,
    val comments: DiscussionCommentsCount,
    payout: DiscussionPayout,
    meta: DiscussionMetadata,
    stats: DiscussionStats
) : DiscussionEntity(
    contentId, author,
    votes, payout, meta, stats
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as PostEntity

        if (community != other.community) return false
        if (content != other.content) return false
        if (comments != other.comments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + community.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + comments.hashCode()
        return result
    }

    override fun toString(): String {
        return "PostEntity(community=$community, content=$content, comments=$comments)"
    }

}

class CommentEntity(
    contentId: DiscussionIdEntity,
    author: DiscussionAuthorEntity,
    val content: CommentContent,
    votes: DiscussionVotes,
    payout: DiscussionPayout,
    val parentPostId: DiscussionIdEntity,
    val parentCommentId: DiscussionIdEntity?,
    meta: DiscussionMetadata,
    stats: DiscussionStats
) : DiscussionEntity(
    contentId, author,
    votes, payout, meta, stats
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as CommentEntity

        if (content != other.content) return false
        if (parentPostId != other.parentPostId) return false
        if (parentCommentId != other.parentCommentId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + parentPostId.hashCode()
        result = 31 * result + (parentCommentId?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "CommentEntity(content=$content, parentPostId=$parentPostId, parentCommentId=$parentCommentId)"
    }

}

sealed class DiscussionEntity(
    val contentId: DiscussionIdEntity,
    val author: DiscussionAuthorEntity,
    val votes: DiscussionVotes,
    val payout: DiscussionPayout,
    val meta: DiscussionMetadata,
    val stats: DiscussionStats
) : Entity {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiscussionEntity

        if (contentId != other.contentId) return false
        if (author != other.author) return false
        if (votes != other.votes) return false
        if (payout != other.payout) return false
        if (meta != other.meta) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contentId.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + votes.hashCode()
        result = 31 * result + payout.hashCode()
        result = 31 * result + meta.hashCode()
        return result
    }

    override fun toString(): String {
        return "DiscussionEntity(contentId=$contentId, author=$author, votes=$votes, payout=$payout, meta=$meta)"
    }

}




