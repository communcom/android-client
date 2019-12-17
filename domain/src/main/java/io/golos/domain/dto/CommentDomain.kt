package io.golos.domain.dto

import io.golos.domain.use_cases.post.post_dto.PostBlock

data class CommentDomain(
    val contentId: ContentIdDomain,
    val author: AuthorDomain,
    var votes: VotesDomain,
    val body: PostBlock?,
    val childCommentsCount: Int,
    val community: PostDomain.CommunityDomain,
    val meta: MetaDomain,
    val parent: ParentCommentDomain,
    val type: String,
    val isDeleted: Boolean,
    val isMyComment: Boolean
) {
    enum class CommentTypeDomain {
        USER,
        POST,
        REPLIES
    }
}