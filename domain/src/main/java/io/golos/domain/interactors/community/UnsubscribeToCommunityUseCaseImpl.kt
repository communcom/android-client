package io.golos.domain.interactors.community

import javax.inject.Inject

class UnsubscribeToCommunityUseCaseImpl @Inject constructor(private val communityRepository: CommunitiesRepository): UnsubscribeToCommunityUseCase {

    override suspend fun unsubscribeToCommunity(communityId: String) {
        return communityRepository.unsubscribeToCommunity(communityId)
    }
}