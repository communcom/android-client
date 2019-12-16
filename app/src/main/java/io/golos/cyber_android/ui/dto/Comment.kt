package io.golos.cyber_android.ui.dto

import io.golos.domain.use_cases.post.post_dto.PostBlock

data class Comment(
    val contentId: ContentId,
    val author: Author,
    var votes: Votes,
    val body: PostBlock?,
    val childCommentsCount: Int,
    val community: Post.Community,
    val meta: Meta,
    val parent: ParentComment,
    val type: String
)