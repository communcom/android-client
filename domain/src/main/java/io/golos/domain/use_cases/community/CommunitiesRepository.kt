package io.golos.domain.use_cases.community

import io.golos.domain.dto.*

interface CommunitiesRepository {
    suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun subscribeToCommunity(communityId: CommunityIdDomain)

    suspend fun unsubscribeToCommunity(communityId: CommunityIdDomain)

    suspend fun getCommunityById(communityId: CommunityIdDomain): CommunityPageDomain

    suspend fun getCommunityIdByAlias(alias: String): CommunityIdDomain

    suspend fun getCommunitiesList(
        userId: UserIdDomain,
        offset: Int,
        pageSize: Int,
        showAll: Boolean,
        searchQuery: String? = null): List<CommunityDomain>

    suspend fun getCommunityLeads(communityId: CommunityIdDomain): List<CommunityLeaderDomain>

    suspend fun getCommunitiesInBlackList(offset: Int, pageSize: Int, userId: UserIdDomain): List<CommunityDomain>

    suspend fun moveCommunityToBlackList(communityId: CommunityIdDomain)

    suspend fun moveCommunityFromBlackList(communityId: CommunityIdDomain)
    suspend fun sendCommunitiesCollection(communityIds: List<String>)

    fun saveCommunitySubscriptions(communitySubscriptions: List<CommunityDomain>)

    suspend fun getCommunitySubscriptions(): List<CommunityDomain>

    suspend fun voteForLeader(communityId: CommunityIdDomain, leader: UserIdDomain)

    suspend fun unvoteForLeader(communityId: CommunityIdDomain, leader: UserIdDomain)

    suspend fun getSubscribers(communityId: CommunityIdDomain, offset: Int, pageSizeLimit: Int): List<UserDomain>

    suspend fun getUserCommunities(userIdDomain: UserIdDomain, offset: Int, pageSizeLimit: Int): List<CommunityDomain>
}