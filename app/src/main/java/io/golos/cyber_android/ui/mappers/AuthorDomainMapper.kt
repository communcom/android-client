package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

fun PostDomain.AuthorDomain.mapToAuthor(): Post.Author {
    return Post.Author(
        this.avatarUrl,
        this.userId,
        this.username
    )
}