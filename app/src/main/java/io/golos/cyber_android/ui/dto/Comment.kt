package io.golos.cyber_android.ui.dto

import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.DonationsDomain
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ContentBlock

data class Comment(
    val contentId: ContentIdDomain,
    val author: UserBriefDomain,
    var votes: Votes,
    val body: ContentBlock?,
    val childCommentsCount: Int,
    val community: Post.Community,
    val meta: Meta,
    val parent: ParentComment,
    val type: String,
    val isDeleted: Boolean,
    val isMyComment: Boolean,
    val commentLevel: Int = 0,
    val donations: DonationsDomain?
)