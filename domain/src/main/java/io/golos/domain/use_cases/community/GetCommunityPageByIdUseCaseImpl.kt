package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityPageDomain
import javax.inject.Inject

class GetCommunityPageByIdUseCaseImpl @Inject constructor(private val communitiesRepository: CommunitiesRepository) :
    GetCommunityPageByIdUseCase {

    override suspend fun getCommunityPageById(communityId: String): CommunityPageDomain {
        return communitiesRepository.getCommunityPageById(communityId)
    }
}