package io.golos.data.mappers

import io.golos.commun4j.services.model.GetCommunitiesItem
import io.golos.domain.dto.CommunityDomain

object GetCommunitiesItemToCommunityDomainMapper {
    fun invoke(source: GetCommunitiesItem): CommunityDomain {
        return CommunityDomain(
            communityId = source.communityId,
            name = source.name,
            logo = source.avatarUrl,
            followersCount = source.subscribersCount.toLong(),
            postsCount = source.postsCount?.toLong() ?: 0L,
            isSubscribed = source.isSubscribed ?: false
        )
    }
}