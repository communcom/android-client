package io.golos.use_cases.voting

interface VotingUseCase {
    suspend fun upVote(communityId: String, userId: String, permlink: String)

    suspend fun downVote(communityId: String, userId: String, permlink: String)
}