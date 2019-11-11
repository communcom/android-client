package io.golos.domain.use_cases.community

interface UnsubscribeToCommunityUseCase {

    suspend fun unsubscribeToCommunity(communityId: String)
}