package io.golos.cyber_android.ui.screens.ftue_search_community.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.dto.CommunityDomain

interface FtueSearchCommunityModel : ModelBase {

    suspend fun getCommunities(offset: Int, pageCount: Int): List<CommunityDomain>

    suspend fun onFollowToCommunity(communityId: String)

    suspend fun onUnFollowFromCommunity(communityId: String)

}