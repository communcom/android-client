package io.golos.domain.dto

import io.golos.domain.use_cases.post.post_dto.PostBlock

data class CommentDomain(
    val contentId: ContentIdDomain,
    val authorDomain: AuthorDomain,
    val votes: VotesDomain,
    val body: PostBlock?,
    val childCommentsCount: Int,
    val community: PostDomain.CommunityDomain,
    val meta: MetaDomain,
    val parent: ParentCommentDomain,
    val type: String
) {
    enum class CommentTypeDomain {
        USER,
        POST,
        REPLIES
    }
}