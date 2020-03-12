package io.golos.cyber_android.ui.screens.post_view.model.voting.post

import io.golos.use_cases.voting.VotingUseCase
import io.golos.domain.dto.PostDomain

interface PostPageVotingUseCase : VotingUseCase {
    suspend fun upVote(post: PostDomain, communityId: String, userId: String, permlink: String): PostDomain

    suspend fun downVote(post: PostDomain, communityId: String, userId: String, permlink: String): PostDomain
}