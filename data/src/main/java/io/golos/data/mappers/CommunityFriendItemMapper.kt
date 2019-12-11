package io.golos.data.mappers

import io.golos.commun4j.services.model.CommunityFriendItem
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.dto.CommunityPageDomain

fun CommunityFriendItem.mapToCommunityFriendDomain(leaders: List<CyberName>): CommunityPageDomain.CommunityFriendDomain =
    CommunityPageDomain.CommunityFriendDomain(
        userId = userId.name,
        userName = username ?: "",
        avatarUrl = avatarUrl ?: "",
        isLead = leaders.contains(userId)
    )
