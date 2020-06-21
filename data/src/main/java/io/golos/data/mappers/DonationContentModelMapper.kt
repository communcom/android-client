package io.golos.data.mappers

import io.golos.commun4j.services.model.DonationContentModel
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain

fun DonationContentModel.mapToContentIdDomain(): ContentIdDomain =
    ContentIdDomain(
        communityId = CommunityIdDomain(this.communityId),
        permlink = this.permlink,
        userId = UserIdDomain(this.userId)
    )