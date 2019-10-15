package io.golos.cyber_android.ui.screens.followers.mappers

import io.golos.cyber_android.ui.screens.followers.Follower
import io.golos.domain.entities.FollowerDomain

class FollowerDomainToFollowerMapper : Function1<FollowerDomain, Follower> {

    override fun invoke(follower: FollowerDomain): Follower {
        return Follower(
            follower.userId,
            follower.firstName,
            follower.lastName,
            follower.avatarUrl
        )
    }
}