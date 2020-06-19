package io.golos.domain.dto

import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ContentBlock

data class CommentDomain(
    val contentId: ContentIdDomain,
    val author: UserBriefDomain,
    var votes: VotesDomain,
    val body: ContentBlock?,
    val jsonBody: String?,
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