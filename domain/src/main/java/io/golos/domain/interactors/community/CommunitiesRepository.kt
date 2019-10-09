package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain

interface CommunitiesRepository {

    suspend fun getCommunitiesByQuery(query: String?, sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain

    suspend fun getRecommendedCommunities(sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain

    suspend fun subscribeToCommunity(communityId: String)

    suspend fun unsubscribeToCommunity(communityId: String)
}