package io.golos.cyber_android.ui.screens.followers.mappers

import io.golos.cyber_android.ui.screens.followers.Follower
import io.golos.domain.dto.FollowerDomain

class FollowersDomainListToFollowersListMapper : Function1<List<FollowerDomain>, List<Follower>> {


    override fun invoke(followersDomainList: List<FollowerDomain>): List<Follower> {
        val followersList = mutableListOf<Follower>()
        followersDomainList.forEach {
            followersList.add(FollowerDomainToFollowerMapper().invoke(it))
        }
        return followersList
    }
}