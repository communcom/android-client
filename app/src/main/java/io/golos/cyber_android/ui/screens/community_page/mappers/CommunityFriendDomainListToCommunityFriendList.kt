package io.golos.cyber_android.ui.screens.community_page.mappers

import io.golos.cyber_android.ui.screens.community_page.CommunityPage
import io.golos.domain.entities.CommunityPageDomain

class CommunityFriendDomainListToCommunityFriendList :
    Function1<List<CommunityPageDomain.CommunityFriendDomain>, List<CommunityPage.CommunityFriend>> {

    override fun invoke(communityFriendDomain: List<CommunityPageDomain.CommunityFriendDomain>): List<CommunityPage.CommunityFriend> {
        val friends: MutableList<CommunityPage.CommunityFriend> = mutableListOf()
        communityFriendDomain.forEach {
            friends.add(CommunityFriendDomainToCommunityFriendMapper().invoke(it))
        }
        return friends
    }
}