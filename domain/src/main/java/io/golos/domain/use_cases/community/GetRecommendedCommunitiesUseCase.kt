package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityDomain

interface GetRecommendedCommunitiesUseCase {

    suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain>
}