package io.golos.cyber_android.ui.screens.profile_black_list.view.list

import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain

interface BlackListListItemEventsProcessor {
    fun onNextPageReached(filter: BlackListFilter)

    fun retry(filter: BlackListFilter)

    fun onHideUserClick(userId: UserIdDomain)

    fun onHideCommunityClick(communityId: CommunityIdDomain)
}