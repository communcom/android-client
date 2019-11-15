package io.golos.data.mappers

import io.golos.commun4j.services.model.GetCommunitiesItem
import io.golos.commun4j.sharedmodel.CyberName
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

fun GetCommunitiesItem.mapToCommunityPageDomain(leaders: List<CyberName>): CommunityPageDomain =
    CommunityPageDomain(
        communityId = communityId,
        name = name,
        avatarUrl = avatarUrl,
        coverUrl = coverUrl,
        description = description,
        rules = (rules as? String) ?: "" ,
        isSubscribed = isSubscribed ?: false,
        isBlocked = isBlocked ?: true,
        friendsCount = friendsCount?.toLong() ?: 0,
        friends = friends?.map { it.mapToCommunityFriendDomain(leaders) } ?: listOf(),
        membersCount = subscribersCount.toLong(),
        leadsCount = leadersCount?.toLong() ?: 0,
        communityCurrency = CommunityPageDomain.CommunityPageCurrencyDomain(name, 1f),
        joinDate = Date()
    )