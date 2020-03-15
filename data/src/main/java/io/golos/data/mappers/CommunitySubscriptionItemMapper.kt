package io.golos.data.mappers

import io.golos.commun4j.services.model.CommunitySubscriptionItem
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityIdDomain

fun CommunitySubscriptionItem.mapToCommunityDomain(): CommunityDomain =
    CommunityDomain(
        communityId = CommunityIdDomain(this.communityId.value),
        alias = this.alias,
        name = this.name ?: "",
        avatarUrl = this.avatarUrl,
        coverUrl = this.coverUrl,
        subscribersCount = this.subscribersCount ?: 0,
        postsCount = this.postsCount ?: 0,
        isSubscribed = this.isSubscribed ?: false
    )
