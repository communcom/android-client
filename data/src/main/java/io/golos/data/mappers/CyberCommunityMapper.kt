package io.golos.data.mappers

import io.golos.commun4j.services.model.CyberCommunity
import io.golos.domain.dto.PostDomain

fun CyberCommunity.mapToCommunityDomain(): PostDomain.CommunityDomain{
    return PostDomain.CommunityDomain(
        this.alias,
        this.communityId,
        this.name,
        this.avatarUrl,
        this.isSubscribed ?: false
    )
}