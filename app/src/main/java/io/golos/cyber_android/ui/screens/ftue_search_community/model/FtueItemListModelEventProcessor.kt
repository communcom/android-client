package io.golos.cyber_android.ui.screens.ftue_search_community.model

interface FtueItemListModelEventProcessor :
    FtueCommunityEventProcessor,
    FtueCommunityProgressEventProcessor

interface FtueCommunityEventProcessor {
    fun onFollowToCommunity(communityId: String)

    fun onUnFollowFromCommunity(communityId: String)
}

interface FtueCommunityProgressEventProcessor {
    fun onRetryLoadCommunity()
}