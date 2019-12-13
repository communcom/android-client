package io.golos.domain.dto

data class CommentDomain(
    val commentId:String,
    val avatarUrl: String?,
    val userId: String,
    val username: String?,
    val voteCount: Long,
    val hasUpVote: Boolean,
    val hasDownVote: Boolean,
    val commentText: String
) {
    enum class CommentTypeDomain{
        USER,
        POST,
        REPLIES
    }
}