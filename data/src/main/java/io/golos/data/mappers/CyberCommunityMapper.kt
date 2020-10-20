package io.golos.data.mappers

import io.golos.commun4j.services.model.CyberCommunity
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityIdDomain

fun CyberCommunity.mapToCommunityDomain(): CommunityDomain {
    return CommunityDomain(
        communityId = CommunityIdDomain(this.communityId),
        alias = this.alias,
        name = this.name ?: "",
        avatarUrl = this.avatarUrl,
        coverUrl = null,
        subscribersCount = 0,
        postsCount = 0,
        isSubscribed = this.isSubscribed ?: false
    )
}