package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.AuthorDomain

fun AuthorDomain.mapToAuthor(): Post.Author {
    return Post.Author(
        this.avatarUrl,
        this.userId,
        this.username
    )
}