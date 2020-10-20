package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityIdDomain

interface BlockUnblockCommunityUseCase {

    suspend fun blockCommunity(communityIdDomain: CommunityIdDomain)

    suspend fun unBlockCommunity(communityIdDomain: CommunityIdDomain)

}