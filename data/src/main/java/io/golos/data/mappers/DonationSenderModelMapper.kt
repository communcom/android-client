package io.golos.data.mappers

import io.golos.commun4j.services.model.DonationSenderModel
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.UserIdDomain

fun DonationSenderModel.mapToUserBriefDomain() =
    UserBriefDomain(
        avatarUrl = this.avatarUrl,
        userId = UserIdDomain(this.userId!!),
        username = this.username
    )