package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain

class GetCommunitiesUseCaseImpl : GetCommunitiesUseCase {

    override suspend fun getCommunitiesByQuery(query: String?, sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain {
        return CommunityPageDomain("", emptyList())
    }
}