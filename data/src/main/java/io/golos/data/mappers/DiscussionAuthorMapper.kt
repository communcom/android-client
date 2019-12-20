package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionAuthor
import io.golos.domain.dto.AuthorDomain

fun DiscussionAuthor.mapToAuthorDomain(): AuthorDomain {
    return AuthorDomain(
        this.avatarUrl,
        this.userId.name,
        this.username
    )
}