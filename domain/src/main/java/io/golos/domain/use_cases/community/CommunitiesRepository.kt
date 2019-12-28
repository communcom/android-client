package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityLeaderDomain
import io.golos.domain.dto.CommunityPageDomain
import io.golos.domain.dto.UserIdDomain

interface CommunitiesRepository {
    suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun subscribeToCommunity(communityId: String)

    suspend fun unsubscribeToCommunity(communityId: String)

    suspend fun getCommunityPageById(communityId: String): CommunityPageDomain

    suspend fun getCommunitiesList(
        userId: UserIdDomain,
        offset: Int,
        pageSize: Int,
        showAll: Boolean,
        searchQuery: String? = null): List<CommunityDomain>

    suspend fun getCommunityLeads(communityId: String): List<CommunityLeaderDomain>

    suspend fun getCommunitiesInBlackList(offset: Int, pageSize: Int, userId: UserIdDomain): List<CommunityDomain>

    suspend fun moveCommunityToBlackList(communityId: String)

    suspend fun moveCommunityFromBlackList(communityId: String)
    suspend fun sendCommunitiesCollection(communityIds: List<String>)

    fun saveCommunitySubscriptions(communitySubscriptions: List<CommunityDomain>)

    suspend fun getCommunitySubscriptions(): List<CommunityDomain>

    suspend fun voteForLeader(communityId: String, leader: UserIdDomain)

    suspend fun unvoteForLeader(communityId: String, leader: UserIdDomain)
}