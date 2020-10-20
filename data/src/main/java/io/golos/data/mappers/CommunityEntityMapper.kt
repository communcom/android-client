package io.golos.data.mappers

import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityEntity
import io.golos.domain.dto.CommunityIdDomain

fun List<CommunityEntity>.mapToCommunityDomainList(): List<CommunityDomain> = map { community ->
    community.mapToCommunityDomain()
}

fun CommunityEntity.mapToCommunityDomain() =
    CommunityDomain(
        communityId = CommunityIdDomain(communityId),
        alias = alias,
        name = name,
        avatarUrl = avatarUrl,
        coverUrl = coverUrl,
        subscribersCount = subscribersCount,
        postsCount = postsCount,
        isSubscribed = isSubscribed
    )