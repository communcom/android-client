package io.golos.domain.use_cases.community

interface SubscribeToCommunityUseCase {

    suspend fun subscribeToCommunity(communityId: String)
}