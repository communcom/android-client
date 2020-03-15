package io.golos.use_cases.voting

import io.golos.domain.dto.CommunityIdDomain

interface VotingUseCase {
    suspend fun upVote(communityId: CommunityIdDomain, userId: String, permlink: String)

    suspend fun downVote(communityId: CommunityIdDomain, userId: String, permlink: String)
}