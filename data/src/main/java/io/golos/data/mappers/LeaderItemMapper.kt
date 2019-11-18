package io.golos.data.mappers

import io.golos.commun4j.services.model.LeaderItem
import io.golos.domain.dto.CommunityLeaderDomain

fun LeaderItem.mapToCommunityLeaderDomain() =
    CommunityLeaderDomain(
        userId  = userId,
        avatarUrl = avatarUrl,
        isActive = isActive ?: false,
        isSubscribed = isSubscribed ?: false,
        isVoted = isVoted ?: false,
        position = position ?: 0,
        rating = rating ?: 0.0,
        ratingPercent = ratingPercent ?: 0.0,
        url = url,
        username = username ?: "",
        votesCount = votesCount ?: 0
    )
