package io.golos.cyber_android.ui.screens.subscriptions.mappers

import io.golos.cyber_android.ui.screens.subscriptions.Community
import io.golos.domain.dto.CommunityDomain

class CommunityDomainToCommunityMapper: Function1<CommunityDomain, Community> {


    override fun invoke(communityDomain: CommunityDomain): Community {
        return Community(communityDomain.communityId, communityDomain.name, communityDomain.logo, communityDomain.followersCount, communityDomain.isSubscribed)
    }
}