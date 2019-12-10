package io.golos.data.mappers

import io.golos.data.dto.CommunityEntity
import io.golos.domain.dto.CommunityDomain

fun List<CommunityDomain>.mapToCommunityEntityList(): List<CommunityEntity> = map { community ->
    community.mapToCommunityEntity()
}

fun CommunityDomain.mapToCommunityEntity() =
    CommunityEntity(
        communityId = communityId,
        alias = alias,
        name = name,
        avatarUrl = avatarUrl,
        coverUrl = coverUrl,
        subscribersCount = subscribersCount,
        postsCount = postsCount,
        isSubscribed = isSubscribed
    )