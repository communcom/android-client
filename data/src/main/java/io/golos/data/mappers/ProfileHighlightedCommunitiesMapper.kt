package io.golos.data.mappers

import io.golos.commun4j.services.model.GetProfileResult
import io.golos.domain.dto.CommunityDomain

fun GetProfileResult.ProfileHighlightedCommunitiesItem.mapToCommunityDomain() =
    CommunityDomain(
        communityId = communityId,

        alias = alias,
        name = name!!,

        avatarUrl = avatarUrl,
        coverUrl = coverUrl,

        subscribersCount = subscribersCount ?: 0,
        postsCount = postsCount ?: 0,
        isSubscribed = isSubscribed ?: false
    )
