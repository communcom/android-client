package io.golos.cyber_android.ui.screens.community_page.mappers

import io.golos.cyber_android.ui.screens.community_page.CommunityPage
import io.golos.cyber_android.ui.screens.subscriptions.Community
import io.golos.domain.entities.CommunityDomain
import io.golos.domain.entities.CommunityPageDomain

class CommunityPageDomainToCommunityPageMapper: Function1<CommunityPageDomain, CommunityPage> {

    override fun invoke(communityPageDomain: CommunityPageDomain): CommunityPage {
        return CommunityPage(communityPageDomain.communityId,
            communityPageDomain.name,
            communityPageDomain.avatarUrl,
            communityPageDomain.coverUrl,
            communityPageDomain.description,
            communityPageDomain.rules,
            communityPageDomain.isSubscribed,
            communityPageDomain.isBlocked,
            communityPageDomain.friendsCount,
            CommunityFriendDomainListToCommunityFriendList().invoke(communityPageDomain.friends),
            communityPageDomain.membersCount,
            communityPageDomain.leadsCount,
            CommunityPageCurrencyDomainToCommunityPageCurrency().invoke(communityPageDomain.communityCurrency),
            communityPageDomain.joinDate)
    }
}