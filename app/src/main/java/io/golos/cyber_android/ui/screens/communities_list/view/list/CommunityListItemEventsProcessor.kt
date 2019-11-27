package io.golos.cyber_android.ui.screens.communities_list.view.list

import io.golos.domain.dto.CommunityDomain

interface CommunityListItemEventsProcessor {
    fun onItemClick(community: CommunityDomain)

    fun onNextPageReached()

    fun retry()

    fun onJoinClick(communityId: String) {}
}