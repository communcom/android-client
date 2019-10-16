package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityDomain
import javax.inject.Inject

class GetRecommendedCommunitiesUseCaseImpl @Inject constructor(private val communitiesRepository: CommunitiesRepository) : GetRecommendedCommunitiesUseCase {

    override suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        return communitiesRepository.getRecommendedCommunities(offset, pageLimitSize)
    }
}