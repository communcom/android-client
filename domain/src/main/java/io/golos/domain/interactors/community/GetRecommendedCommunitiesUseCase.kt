package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain

interface GetRecommendedCommunitiesUseCase {

    suspend fun getRecommendedCommunities(sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain
}