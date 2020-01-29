package io.golos.domain.dto

import io.golos.domain.use_cases.post.post_dto.ContentBlock

data class CommentDomain(
    val contentId: ContentIdDomain,
    val author: AuthorDomain,
    var votes: VotesDomain,
    val body: ContentBlock?,
    val childCommentsCount: Int,
    val community: CommunityDomain,
    val meta: MetaDomain,
    val parent: ParentCommentDomain,
    val type: String,
    val isDeleted: Boolean,
    val isMyComment: Boolean,
    val commentLevel: Int = 0
) {
    enum class CommentTypeDomain {
        USER,
        POST,
        REPLIES
    }
}