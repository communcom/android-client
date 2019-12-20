package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Author
import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.AuthorDomain

fun AuthorDomain.mapToAuthor(): Author {
    return Author(
        this.avatarUrl,
        this.userId,
        this.username
    )
}