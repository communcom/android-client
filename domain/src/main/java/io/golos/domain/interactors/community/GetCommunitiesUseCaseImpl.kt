package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain
import javax.inject.Inject

class GetCommunitiesUseCaseImpl @Inject constructor() : GetCommunitiesUseCase {

    override suspend fun getCommunitiesByQuery(query: String?, sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain {
        return CommunityPageDomain("", emptyList())
    }
}