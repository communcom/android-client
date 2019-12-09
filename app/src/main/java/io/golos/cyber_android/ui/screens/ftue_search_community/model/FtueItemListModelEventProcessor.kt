package io.golos.cyber_android.ui.screens.ftue_search_community.model

import io.golos.cyber_android.ui.dto.Community

interface FtueItemListModelEventProcessor :
    FtueCommunityEventProcessor,
    FtueCommunityProgressEventProcessor,
    FtueCommunityCollectionEventProcessor

interface FtueCommunityEventProcessor {
    fun onFollowToCommunity(community: Community)

    fun onUnFollowFromCommunity(community: Community)
}

interface FtueCommunityProgressEventProcessor {
    fun onRetryLoadCommunity()
}

interface FtueCommunityCollectionEventProcessor {
    fun removeCommunityFromCollection(community: Community)
}