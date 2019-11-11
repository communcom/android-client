package io.golos.cyber_android.ui.screens.community_page.mappers

import io.golos.cyber_android.ui.screens.community_page.CommunityPage
import io.golos.domain.dto.CommunityPageDomain

class CommunityFriendDomainToCommunityFriendMapper :
    Function1<CommunityPageDomain.CommunityFriendDomain, CommunityPage.CommunityFriend> {

    override fun invoke(communityFriendDomain: CommunityPageDomain.CommunityFriendDomain): CommunityPage.CommunityFriend {

        return CommunityPage.CommunityFriend(
            communityFriendDomain.userId,
            communityFriendDomain.userName,
            communityFriendDomain.avatarUrl,
            communityFriendDomain.hasAward
        )
    }
}