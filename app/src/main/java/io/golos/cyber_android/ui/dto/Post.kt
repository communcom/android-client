package io.golos.cyber_android.ui.dto

import io.golos.domain.use_cases.post.post_dto.PostBlock

data class Post(

    val author: Author,
    val community: Community,
    val contentId: ContentId,
    val body: PostBlock?,
    val meta: Meta,
    val stats: Stats?,
    val type: String?,
    val shareUrl: String?,
    var votes: Votes,
    val isMyPost: Boolean
) {

    data class Community(
        val alias: String?,
        val communityId: String,
        val name: String?,
        val avatarUrl: String?,
        var isSubscribed: Boolean
    )
}
