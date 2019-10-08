package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain

class GetRecommendedCommunitiesUseCaseImpl : GetRecommendedCommunitiesUseCase{


    override suspend fun getRecommendedCommunities(sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain {
        return CommunityPageDomain("", emptyList())
    }
}