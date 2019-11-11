package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityDomain

interface GetCommunitiesUseCase {

    /**
     * Get followers by query
     */
    suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain>
}