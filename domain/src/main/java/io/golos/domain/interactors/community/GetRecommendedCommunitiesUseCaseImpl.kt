package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain
import javax.inject.Inject

class GetRecommendedCommunitiesUseCaseImpl @Inject constructor() : GetRecommendedCommunitiesUseCase {


    override suspend fun getRecommendedCommunities(sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain {
        return CommunityPageDomain("", emptyList())
    }
}