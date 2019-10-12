package io.golos.data.repositories

import io.golos.data.api.communities.CommunitiesApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.CommunityPageDomain
import io.golos.domain.interactors.community.CommunitiesRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommunitiesRepositoryImpl @Inject constructor(
    private val communitiesApi: CommunitiesApi,
    private val dispatchersProvider: DispatchersProvider
) :
    CommunitiesRepository {

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

    override suspend fun getCommunitiesByQuery(query: String?, sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain {
        return withContext(dispatchersProvider.ioDispatcher) {
            communitiesApi.getCommunitiesByQuery(query, sequenceKey, pageLimitSize)
        }
    }

    override suspend fun getRecommendedCommunities(sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain {
        return withContext(dispatchersProvider.ioDispatcher) {
            communitiesApi.getRecommendedCommunities(sequenceKey, pageLimitSize)
        }
    }
}