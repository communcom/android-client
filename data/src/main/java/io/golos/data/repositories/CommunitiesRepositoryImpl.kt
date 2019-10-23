package io.golos.data.repositories

import io.golos.data.api.communities.CommunitiesApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.CommunityDomain
import io.golos.domain.entities.CommunityPageDomain
import io.golos.domain.interactors.community.CommunitiesRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommunitiesRepositoryImpl @Inject constructor(
    private val communitiesApi: CommunitiesApi,
    private val dispatchersProvider: DispatchersProvider
) :
    CommunitiesRepository {

    override suspend fun getCommunityPageById(communityId: String): CommunityPageDomain {
        return withContext(dispatchersProvider.ioDispatcher){
            communitiesApi.getCommunityPageById(communityId)
        }
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
}