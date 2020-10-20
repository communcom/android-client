package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityIdDomain
import javax.inject.Inject

class SubscribeToCommunityUseCaseImpl @Inject constructor(private val communityRepository: CommunitiesRepository) :
    SubscribeToCommunityUseCase {

    override suspend fun subscribeToCommunity(communityId: CommunityIdDomain) {
        communityRepository.subscribeToCommunity(communityId)
    }
}