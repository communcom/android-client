package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityDomain
import javax.inject.Inject

class GetCommunitiesUseCaseImpl @Inject constructor(private val communitiesRepository: CommunitiesRepository) : GetCommunitiesUseCase {

    override suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain> {
        return communitiesRepository.getCommunitiesByQuery(query, offset, pageLimitSize)
    }
}