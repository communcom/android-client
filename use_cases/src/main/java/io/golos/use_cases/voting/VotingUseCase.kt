package io.golos.use_cases.voting

import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain

interface VotingUseCase {
    suspend fun upVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String)

    suspend fun downVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String)

    suspend fun unVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String)
}