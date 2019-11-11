package io.golos.domain.use_cases.community

import javax.inject.Inject

class SubscribeToCommunityUseCaseImpl @Inject constructor(private val communityRepository: CommunitiesRepository) :
    SubscribeToCommunityUseCase {

    override suspend fun subscribeToCommunity(communityId: String) {
        communityRepository.subscribeToCommunity(communityId)
    }
}