package io.golos.domain.interactors.community

import javax.inject.Inject

class SubscribeToCommunityUseCaseImpl @Inject constructor(private val communityRepository: CommunitiesRepository) :
    SubscribeToCommunityUseCase {

    override suspend fun subscribeToCommunity(communityId: String) {
        communityRepository.subscribeToCommunity(communityId)
    }
}