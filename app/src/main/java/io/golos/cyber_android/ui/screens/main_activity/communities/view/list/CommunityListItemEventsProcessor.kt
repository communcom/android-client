package io.golos.cyber_android.ui.screens.main_activity.communities.view.list

import io.golos.domain.dto.CommunityDomain

interface CommunityListItemEventsProcessor {
    fun onItemClick(community: CommunityDomain)
}