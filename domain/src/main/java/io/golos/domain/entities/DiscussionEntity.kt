package io.golos.domain.entities

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */

sealed class DiscussionEntity(
    val contentId: DiscussionId,
    val author: DiscussionAuthorEntity,
    val community: CommunityEntity,
    val content: DiscussionContent,
    val votes: DiscussionVotes,
    val comments: DiscussionCommentsCount,
    val payout: DiscussionPayout,
    val postId: ParentId?,
    val parentCommentId: ParentId?,
    val meta: DiscussionMetadata
) : Entity {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiscussionEntity

        if (contentId != other.contentId) return false
        if (author != other.author) return false
        if (community != other.community) return false
        if (content != other.content) return false
        if (votes != other.votes) return false
        if (comments != other.comments) return false
        if (payout != other.payout) return false
        if (postId != other.postId) return false
        if (parentCommentId != other.parentCommentId) return false
        if (meta != other.meta) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contentId.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + community.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + votes.hashCode()
        result = 31 * result + comments.hashCode()
        result = 31 * result + payout.hashCode()
        result = 31 * result + (postId?.hashCode() ?: 0)
        result = 31 * result + (parentCommentId?.hashCode() ?: 0)
        result = 31 * result + meta.hashCode()
        return result
    }

    override fun toString(): String {
        return "DiscussionEntity(contentId=$contentId, author=$author, community=$community, content=$content, votes=$votes, comments=$comments, payout=$payout, postId=$postId, parentCommentId=$parentCommentId, meta=$meta)"
    }

}

class PostEntity(
    contentId: DiscussionId,
    author: DiscussionAuthorEntity,
    community: CommunityEntity,
    content: DiscussionContent,
    votes: DiscussionVotes,
    comments: DiscussionCommentsCount,
    payout: DiscussionPayout,
    meta: DiscussionMetadata
) : DiscussionEntity(
    contentId, author, community, content,
    votes, comments, payout, null, null, meta
)

class CommentEntity(
    contentId: DiscussionId,
    author: DiscussionAuthorEntity,
    community: CommunityEntity,
    content: DiscussionContent,
    votes: DiscussionVotes,
    comments: DiscussionCommentsCount,
    payout: DiscussionPayout,
    postId: ParentId,
    parentCommentId: ParentId,
    meta: DiscussionMetadata
) : DiscussionEntity(
    contentId, author, community, content,
    votes, comments, payout, postId, parentCommentId, meta
)


