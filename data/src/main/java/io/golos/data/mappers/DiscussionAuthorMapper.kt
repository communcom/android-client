package io.golos.data.mappers

import io.golos.commun4j.model.DiscussionAuthor
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.UserIdDomain

fun DiscussionAuthor.mapToAuthorDomain(): UserBriefDomain {
    return UserBriefDomain(
        this.avatarUrl,
        UserIdDomain(this.userId.name),
        this.username
    )
}