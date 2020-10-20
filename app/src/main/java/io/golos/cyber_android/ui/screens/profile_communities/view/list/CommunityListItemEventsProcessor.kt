package io.golos.cyber_android.ui.screens.profile_communities.view.list

import io.golos.domain.dto.CommunityIdDomain

interface CommunityListItemEventsProcessor {
    fun onItemClick(communityId: CommunityIdDomain)

    fun onFolllowUnfollowClick(communityId: CommunityIdDomain) {}
}