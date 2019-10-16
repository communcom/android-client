package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityDomain
import javax.inject.Inject

class GetCommunitiesUseCaseImpl @Inject constructor(private val communitiesRepository: CommunitiesRepository) : GetCommunitiesUseCase {

    override suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        return communitiesRepository.getCommunitiesByQuery(query, offset, pageLimitSize)
    }
}