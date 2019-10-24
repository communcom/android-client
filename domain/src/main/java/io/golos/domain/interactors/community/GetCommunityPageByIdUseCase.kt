package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityPageDomain

interface GetCommunityPageByIdUseCase {

    suspend fun getCommunityPageById(communityId: String): CommunityPageDomain
}