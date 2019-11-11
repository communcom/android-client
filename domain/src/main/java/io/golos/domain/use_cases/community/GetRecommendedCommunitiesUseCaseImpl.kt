package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityDomain
import javax.inject.Inject

class GetRecommendedCommunitiesUseCaseImpl @Inject constructor(private val communitiesRepository: CommunitiesRepository) : GetRecommendedCommunitiesUseCase {

    override suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        return communitiesRepository.getRecommendedCommunities(offset, pageLimitSize)
    }
}