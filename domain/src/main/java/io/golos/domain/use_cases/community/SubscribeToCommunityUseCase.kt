package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityIdDomain

interface SubscribeToCommunityUseCase {

    suspend fun subscribeToCommunity(communityId: CommunityIdDomain)
}