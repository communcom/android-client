package io.golos.cyber_android.ui.screens.profile_followers.view.list

import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.domain.dto.UserIdDomain

interface FollowersListItemEventsProcessor {
    fun onNextPageReached(filter: FollowersFilter)

    fun retry(filter: FollowersFilter)

    fun onFollowClick(userId: UserIdDomain, filter: FollowersFilter)
}