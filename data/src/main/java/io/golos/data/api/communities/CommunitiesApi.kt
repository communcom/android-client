package io.golos.data.api.communities

import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.CommunityPageDomain

interface CommunitiesApi {
    /**
     * @param isUser if the value is true returns only user's community (all communities otherwise)
     */
    fun getCommunitiesList(offset: Int, pageSize: Int, isUser: Boolean): List<CommunityDomain>

    fun joinToCommunity(externalId: String)

    /**
     * @param isUser is user's or discoverer followers
     */
    suspend fun searchInCommunities(query: String, isUser: Boolean): List<CommunityDomain>

    fun getCommunityById(communityId: CommunityIdDomain): CommunityDomain?

    suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun subscribeToCommunity(communityId: CommunityIdDomain)

    suspend fun unsubscribeToCommunity(communityId: CommunityIdDomain)

    suspend fun getCommunityPageById(communityId: CommunityIdDomain): CommunityPageDomain
}