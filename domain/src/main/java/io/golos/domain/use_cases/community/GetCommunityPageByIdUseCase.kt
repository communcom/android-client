package io.golos.domain.use_cases.community

import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.CommunityPageDomain

interface GetCommunityPageByIdUseCase {

    suspend fun getCommunityPageById(communityId: CommunityIdDomain): CommunityPageDomain
}