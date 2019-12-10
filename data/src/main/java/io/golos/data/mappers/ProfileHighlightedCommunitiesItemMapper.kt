package io.golos.data.mappers

import io.golos.commun4j.services.model.GetProfileResult
import io.golos.domain.dto.CommunityDomain

fun GetProfileResult.ProfileHighlightedCommunitiesItem.mapToCommunityDomain(): CommunityDomain{
    return CommunityDomain(
        this.communityId,
        this.alias,
        this.name ?: "",
        this.avatarUrl,
        this.coverUrl,
        this.subscribersCount ?: 0,
        this.postsCount ?: 0,
        this.isSubscribed ?: false
    )
}