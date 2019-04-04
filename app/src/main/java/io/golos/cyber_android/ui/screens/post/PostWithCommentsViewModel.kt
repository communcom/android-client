package io.golos.cyber_android.ui.screens.post

import android.util.Log
import io.golos.cyber_android.ui.common.AbstractFeedViewModel
import io.golos.domain.entities.CommentEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.PostWithCommentUseCase
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.model.CommentFeedUpdateRequest
import io.golos.domain.model.VoteRequestModel

class PostWithCommentsViewModel(
    postWithCommentUseCase: PostWithCommentUseCase,
    voteUseCase: VoteUseCase
) : AbstractFeedViewModel<CommentFeedUpdateRequest, CommentEntity, CommentModel>(postWithCommentUseCase, voteUseCase) {

    val postLiveData = postWithCommentUseCase.getPostAsLiveData

    fun onPostDownvote() {
        postLiveData.value?.let {
            val power = if (!it.votes.hasDownVote) (-10_000).toShort() else 0.toShort()
            voteForPost(power, it)
        }
    }

    fun onPostUpote() {
        postLiveData.value?.let {
            val power = if (!it.votes.hasUpVote) 10_000.toShort() else 0.toShort()
            voteForPost(power, it)
        }
    }

    override fun vote(power: Short, discussionModel: CommentModel) {
        if (!discussionModel.votes.hasUpVoteProgress
            && !discussionModel.votes.hasDownVotingProgress
            && !discussionModel.votes.hasVoteCancelProgress
        ) {
            val request = VoteRequestModel.VoteForComentRequest(power, discussionModel.contentId)
            vote(request, discussionModel.contentId)
        }
    }


    fun voteForPost(power: Short, discussionModel: PostModel) {
        if (!discussionModel.votes.hasUpVoteProgress
            && !discussionModel.votes.hasDownVotingProgress
            && !discussionModel.votes.hasVoteCancelProgress
        ) {
            val request = VoteRequestModel.VoteForPostRequest(power, discussionModel.contentId)
            vote(request, discussionModel.contentId)
        }
    }
}