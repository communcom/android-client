package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain
import javax.inject.Inject

class GetCommunitiesUseCaseImpl @Inject constructor(private val communitiesRepository: CommunitiesRepository) : GetCommunitiesUseCase {

    override suspend fun getCommunitiesByQuery(query: String?, sequenceKey: String?, pageLimitSize: Int): CommunityPageDomain {
        return communitiesRepository.getCommunitiesByQuery(query, sequenceKey, pageLimitSize)
    }
}