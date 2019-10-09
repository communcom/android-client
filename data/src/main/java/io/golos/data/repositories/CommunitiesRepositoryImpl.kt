package io.golos.data.repositories

import io.golos.data.api.CommunitiesApi
import io.golos.domain.entities.CommunityPageDomain
import io.golos.domain.interactors.community.CommunitiesRepository
import javax.inject.Inject

class CommunitiesRepositoryImpl @Inject constructor(private val communitiesApi: CommunitiesApi):
    CommunitiesRepository {

    override suspend fun subscribeToCommunity(communityId: String) {
        return communitiesApi.subscribeToCommunity(communityId)
    }

    override suspend fun unsubscribeToCommunity(communityId: String) {
        return communitiesApi.unsubscribeToCommunity(communityId)
    }

    override suspend fun getCommunitiesByQuery(query: String?, sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain {
        return communitiesApi.getCommunitiesByQuery(query, sequenceKey, pageLimitSize)
    }

    override suspend fun getRecommendedCommunities(sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain {
        return communitiesApi.getRecommendedCommunities(sequenceKey, pageLimitSize)
    }
}