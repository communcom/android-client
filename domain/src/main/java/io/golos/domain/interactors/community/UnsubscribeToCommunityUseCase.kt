package io.golos.domain.interactors.community

interface UnsubscribeToCommunityUseCase {

    suspend fun unsubscribeToCommunity(communityId: String)
}