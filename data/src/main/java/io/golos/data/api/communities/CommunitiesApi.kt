package io.golos.data.api.communities

import io.golos.domain.commun_entities.Community
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.entities.CommunityDomain

interface CommunitiesApi {
    /**
     * @param isUser is user's or discoverer followers
     */
    suspend fun getCommunitiesList(offset: Int, pageSize: Int, isUser: Boolean): List<Community>

    suspend fun joinToCommunity(externalId: String)

    /**
     * @param isUser is user's or discoverer followers
     */
    suspend fun searchInCommunities(query: String, isUser: Boolean): List<Community>

    fun getCommunityById(communityId: CommunityId): Community?

    suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun subscribeToCommunity(communityId: String)

    suspend fun unsubscribeToCommunity(communityId: String)
}