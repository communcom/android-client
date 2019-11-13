package io.golos.data.api.communities

import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityPageDomain

interface CommunitiesApi {
    /**
     * @param isUser is user's or discoverer followers
     */
    fun getCommunitiesList(offset: Int, pageSize: Int, isUser: Boolean): List<CommunityDomain>

    suspend fun joinToCommunity(externalId: String)

    /**
     * @param isUser is user's or discoverer followers
     */
    suspend fun searchInCommunities(query: String, isUser: Boolean): List<CommunityDomain>

    fun getCommunityById(communityId: String): CommunityDomain?

    suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun subscribeToCommunity(communityId: String)

    suspend fun unsubscribeToCommunity(communityId: String)

    suspend fun getCommunityPageById(communityId: String): CommunityPageDomain
}