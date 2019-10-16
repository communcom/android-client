package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityDomain

interface GetRecommendedCommunitiesUseCase {

    suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain>
}