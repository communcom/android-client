package io.golos.data.mappers

import io.golos.commun4j.services.model.ProposerModel
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.UserIdDomain


fun ProposerModel.mapToAuthorDomain(): UserBriefDomain {
    return UserBriefDomain(
        this.avatarUrl,
        UserIdDomain(this.userId?:""),
        this.username
    )
}