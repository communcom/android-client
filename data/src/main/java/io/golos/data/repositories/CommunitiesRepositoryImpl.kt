package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.data.api.communities.CommunitiesApi
import io.golos.data.mappers.mapToCommunityDomain
import io.golos.data.mappers.mapToCommunityLeaderDomain
import io.golos.data.mappers.mapToCommunityPageDomain
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityLeaderDomain
import io.golos.domain.dto.CommunityPageDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.community.CommunitiesRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommunitiesRepositoryImpl
@Inject
constructor(
    private val communitiesApi: CommunitiesApi,
    private val dispatchersProvider: DispatchersProvider,

    private val commun4j: Commun4j
) : RepositoryBase(dispatchersProvider),
    CommunitiesRepository {

    override suspend fun getCommunityPageById(communityId: String): CommunityPageDomain {
        val community = apiCall { commun4j.getCommunity(communityId) }
        val leads = apiCall { commun4j.getLeaders(communityId, 50, 0) }.items.map { it.userId }
        return community.mapToCommunityPageDomain(leads)
    }

    override suspend fun subscribeToCommunity(communityId: String) {
        return withContext(dispatchersProvider.ioDispatcher) {
            communitiesApi.subscribeToCommunity(communityId)
        }
    }

    override suspend fun unsubscribeToCommunity(communityId: String) {
        return withContext(dispatchersProvider.ioDispatcher) {
            communitiesApi.unsubscribeToCommunity(communityId)
        }
    }

    override suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            communitiesApi.getCommunitiesByQuery(query, offset, pageLimitSize)
        }
    }

    override suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            communitiesApi.getRecommendedCommunities(offset, pageLimitSize)
        }
    }

    /**
     * [forCurrentUserOnly] if true the method returns only current users' communities (otherwise - all communities)
     */
    override suspend fun getCommunitiesList(offset: Int, pageSize: Int, forCurrentUserOnly: Boolean): List<CommunityDomain> {
        if(forCurrentUserOnly) {
            throw UnsupportedOperationException("Getting communities for current user is not supported now")
        }

        return apiCall { commun4j.getCommunitiesList(null, offset, pageSize) }
            .items
            .map { it.mapToCommunityDomain() }
    }

    override suspend fun getCommunityLeads(communityId: String): List<CommunityLeaderDomain> =
        apiCall { commun4j.getLeaders(communityId, 50, 0)}
            .items
            .map { it.mapToCommunityLeaderDomain() }
}