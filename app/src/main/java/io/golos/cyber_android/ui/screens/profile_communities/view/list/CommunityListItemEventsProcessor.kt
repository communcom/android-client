package io.golos.cyber_android.ui.screens.profile_communities.view.list

import io.golos.cyber_android.ui.dto.Community

interface CommunityListItemEventsProcessor {
    fun onItemClick(communityCode: String)

    fun onFolllowUnfollowClick(communityCode: String) {}
}