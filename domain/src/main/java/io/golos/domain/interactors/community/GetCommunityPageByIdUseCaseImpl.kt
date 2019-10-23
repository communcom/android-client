package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain
import javax.inject.Inject

class GetCommunityPageByIdUseCaseImpl @Inject constructor(private val communitiesRepository: CommunitiesRepository) :
    GetCommunityPageByIdUseCase {

    override suspend fun getCommunityPageById(communityId: String): CommunityPageDomain {
        return communitiesRepository.getCommunityPageById(communityId)
    }
}