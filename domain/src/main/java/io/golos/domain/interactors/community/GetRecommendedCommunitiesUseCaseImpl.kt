package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain
import javax.inject.Inject

class GetRecommendedCommunitiesUseCaseImpl @Inject constructor(private val communitiesRepository: CommunitiesRepository) : GetRecommendedCommunitiesUseCase {

    override suspend fun getRecommendedCommunities(sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain {
        return communitiesRepository.getRecommendedCommunities(sequenceKey, pageLimitSize)
    }
}