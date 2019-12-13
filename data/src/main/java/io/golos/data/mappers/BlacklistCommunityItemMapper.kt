package io.golos.data.mappers

import io.golos.commun4j.services.model.BlacklistCommunityItem
import io.golos.domain.dto.CommunityDomain

fun BlacklistCommunityItem.mapToCommunityDomain(): CommunityDomain =
    CommunityDomain(
        communityId = communityId,
        alias = alias,
        name = name ?: "",
        
        avatarUrl = null,
        coverUrl = null,

        subscribersCount = 0,
        postsCount = 0,
        isSubscribed = isSubscribed ?: false
    )