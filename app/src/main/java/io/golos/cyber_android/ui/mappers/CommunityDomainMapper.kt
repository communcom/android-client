package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.PostDomain

fun CommunityDomain.mapToPostCommunity(): Post.Community {
    return Post.Community(
        this.alias,
        this.communityId,
        this.name,
        this.avatarUrl,
        this.isSubscribed
    )
}

fun CommunityDomain.mapToCommunity() =
    Community (
        communityId = communityId,
        alias = alias,
        name = name,
        avatarUrl = avatarUrl,
        coverUrl = coverUrl,
        subscribersCount = subscribersCount,
        postsCount = postsCount,
        isSubscribed = isSubscribed
    )