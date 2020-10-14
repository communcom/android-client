package io.golos.data.mappers

import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityEntity

fun List<CommunityDomain>.mapToCommunityEntityList(): List<CommunityEntity> = map { community ->
    community.mapToCommunityEntity()
}

fun CommunityDomain.mapToCommunityEntity() =
    CommunityEntity(
        communityId = communityId.code,
        alias = alias,
        name = name,
        avatarUrl = avatarUrl,
        coverUrl = coverUrl,
        subscribersCount = subscribersCount,
        postsCount = postsCount,
        isSubscribed = isSubscribed
    )