package io.golos.cyber_android.ui.screens.post_view.model.voting.comment

import io.golos.domain.use_cases.model.CommentModel

interface CommentVotingUseCase {
    suspend fun upVote(comment: CommentModel, communityId: String, userId: String, permlink: String): CommentModel

    suspend fun downVote(comment: CommentModel, communityId: String, userId: String, permlink: String): CommentModel
}