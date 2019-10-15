package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain

interface GetCommunitiesUseCase {

    /**
     * Get followers by query
     */
    suspend fun getCommunitiesByQuery(query: String?, sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain
}