package io.golos.cyber_android.ui.screens.community_page_members.view

import io.golos.domain.dto.UserIdDomain

interface UsersListEventsProcessor {
    fun onNextPageReached()

    fun retry()

    fun onFollowClick(userId: UserIdDomain)
}