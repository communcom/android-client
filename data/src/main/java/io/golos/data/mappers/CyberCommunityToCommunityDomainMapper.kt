package io.golos.data.mappers

import io.golos.commun4j.services.model.CyberCommunity
import io.golos.domain.dto.PostDomain

class CyberCommunityToCommunityDomainMapper : Function1<CyberCommunity, PostDomain.CommunityDomain> {

    override fun invoke(cyberCommunity: CyberCommunity): PostDomain.CommunityDomain {
        return PostDomain.CommunityDomain(
            cyberCommunity.alias,
            cyberCommunity.communityId,
            cyberCommunity.name,
            cyberCommunity.avatarUrl
        )
    }
}