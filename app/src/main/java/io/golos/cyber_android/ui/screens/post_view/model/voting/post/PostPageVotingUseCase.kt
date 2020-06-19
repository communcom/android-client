package io.golos.cyber_android.ui.screens.post_view.model.voting.post

import io.golos.domain.dto.CommunityIdDomain
import io.golos.use_cases.voting.VotingUseCase
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.UserIdDomain

interface PostPageVotingUseCase : VotingUseCase {
    suspend fun upVote(post: PostDomain, communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String): PostDomain

    suspend fun downVote(post: PostDomain, communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String): PostDomain
}