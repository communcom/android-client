package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityIdDomain
import javax.inject.Inject

class UnsubscribeToCommunityUseCaseImpl @Inject constructor(private val communityRepository: CommunitiesRepository): UnsubscribeToCommunityUseCase {

    override suspend fun unsubscribeToCommunity(communityId: CommunityIdDomain) {
        return communityRepository.unsubscribeToCommunity(communityId)
    }
}