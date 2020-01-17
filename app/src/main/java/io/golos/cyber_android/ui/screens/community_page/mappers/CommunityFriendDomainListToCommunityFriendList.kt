package io.golos.cyber_android.ui.screens.community_page.mappers

import io.golos.cyber_android.ui.screens.community_page.dto.CommunityFriend
import io.golos.domain.dto.CommunityPageDomain

class CommunityFriendDomainListToCommunityFriendList :
    Function1<List<CommunityPageDomain.CommunityFriendDomain>, List<CommunityFriend>> {

    override fun invoke(communityFriendDomain: List<CommunityPageDomain.CommunityFriendDomain>): List<CommunityFriend> {
        val friends: MutableList<CommunityFriend> = mutableListOf()
        communityFriendDomain.forEach {
            friends.add(CommunityFriendDomainToCommunityFriendMapper().invoke(it))
        }
        return friends
    }
}