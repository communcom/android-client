package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityIdDomain

interface UnsubscribeToCommunityUseCase {

    suspend fun unsubscribeToCommunity(communityId: CommunityIdDomain)
}