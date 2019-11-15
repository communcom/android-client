package io.golos.data.mappers

import io.golos.commun4j.services.model.GetCommunitiesItem
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityPageDomain
import java.util.*

fun GetCommunitiesItem.mapToCommunityDomain(): CommunityDomain =
    CommunityDomain(
        communityId = communityId,
        name = name,
        logo = avatarUrl,
        followersCount = subscribersCount.toLong(),
        postsCount = postsCount?.toLong() ?: 0L,
        isSubscribed = isSubscribed ?: false
    )

fun GetCommunitiesItem.mapToCommunityPageDomain(): CommunityPageDomain =
    CommunityPageDomain(
        communityId = communityId,
        name = name,
        avatarUrl = avatarUrl,
        coverUrl = coverImageLink,
        description = description,
        rules = "",
        isSubscribed = isSubscribed ?: false,
        isBlocked = isBlocked ?: true,
        friendsCount = 0,
        friends = listOf(),
        membersCount = subscribersCount.toLong(),
        leadsCount = 0,
        communityCurrency = CommunityPageDomain.CommunityPageCurrencyDomain(name, 1f),
        joinDate = Date()
    )