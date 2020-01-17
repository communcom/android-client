package io.golos.cyber_android.ui.screens.community_page.mappers

import io.golos.cyber_android.ui.screens.community_page.dto.CommunityFriend
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityPage
import io.golos.domain.dto.CommunityPageDomain

class CommunityFriendDomainToCommunityFriendMapper :
    Function1<CommunityPageDomain.CommunityFriendDomain, CommunityFriend> {

    override fun invoke(communityFriendDomain: CommunityPageDomain.CommunityFriendDomain): CommunityFriend {

        return CommunityFriend(
            communityFriendDomain.userId,
            communityFriendDomain.userName,
            communityFriendDomain.avatarUrl,
            communityFriendDomain.isLead
        )
    }
}