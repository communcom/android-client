package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

fun PostDomain.CommunityDomain.mapToCommunity(): Post.Community {
    return Post.Community(
        this.alias,
        this.communityId,
        this.name,
        this.avatarUrl,
        this.isSubscribed
    )
}