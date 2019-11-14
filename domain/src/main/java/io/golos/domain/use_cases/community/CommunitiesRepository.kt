package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityPageDomain

interface CommunitiesRepository {
    suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun subscribeToCommunity(communityId: String)

    suspend fun unsubscribeToCommunity(communityId: String)

    suspend fun getCommunityPageById(communityId: String): CommunityPageDomain

    /**
     * [forCurrentUserOnly] if true the method returns only current users' communities (otherwise - all communities)
     */
    fun getCommunitiesList(offset: Int, pageSize: Int, forCurrentUserOnly: Boolean): List<CommunityDomain>
}