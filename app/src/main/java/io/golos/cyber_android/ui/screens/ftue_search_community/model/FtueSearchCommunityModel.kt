package io.golos.cyber_android.ui.screens.ftue_search_community.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.FtueBoardStageDomain

interface FtueSearchCommunityModel : ModelBase {

    suspend fun getCommunities(query: String?, offset: Int, pageSize: Int): List<CommunityDomain>

    suspend fun onFollowToCommunity(communityCode: String)

    suspend fun onUnFollowFromCommunity(communityCode: String)

    suspend fun sendCommunitiesCollection(communityCodes: List<String>)

    suspend fun setFtueBoardStage(stage: FtueBoardStageDomain)

    fun saveCommunitySubscriptions(communitySubscriptions: List<CommunityDomain>)

    suspend fun getCommunitySubscriptions(): List<CommunityDomain>
}