package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityDomain

interface GetCommunitiesUseCase {

    /**
     * Get followers by query
     */
    suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain>
}