package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

class CommunityDomainToCommunityMapper : Function1<PostDomain.CommunityDomain, Post.Community> {

    override fun invoke(communityDomain: PostDomain.CommunityDomain): Post.Community {
        return Post.Community(
            communityDomain.alias,
            communityDomain.communityId,
            communityDomain.name
        )
    }
}