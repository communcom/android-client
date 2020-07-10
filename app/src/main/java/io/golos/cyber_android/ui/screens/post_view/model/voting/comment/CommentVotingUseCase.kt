package io.golos.cyber_android.ui.screens.post_view.model.voting.comment

import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain

interface CommentVotingUseCase {
    suspend fun upVote(comment: CommentDomain, communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String): CommentDomain

    suspend fun downVote(comment: CommentDomain, communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String): CommentDomain
}