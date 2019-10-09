package io.golos.domain.interactors.community

interface SubscribeToCommunityUseCase {

    suspend fun subscribeToCommunity(communityId: String)
}