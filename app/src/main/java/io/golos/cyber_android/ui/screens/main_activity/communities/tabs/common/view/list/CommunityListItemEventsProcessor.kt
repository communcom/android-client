package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list

import io.golos.domain.commun_entities.Community

interface CommunityListItemEventsProcessor {
    fun onItemClick(community: Community)
}