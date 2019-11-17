package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionAuthor
import io.golos.domain.dto.PostDomain

fun DiscussionAuthor.mapToAuthorDomain(): PostDomain.AuthorDomain{
    return PostDomain.AuthorDomain(this.avatarUrl,
        this.userId.name,
        this.username)
}