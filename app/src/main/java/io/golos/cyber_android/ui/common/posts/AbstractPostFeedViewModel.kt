package io.golos.cyber_android.ui.common.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.ui.common.AbstractFeedViewModel
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.model.PostFeedUpdateRequest
import io.golos.domain.model.VoteRequestModel

/**
 * Base [ViewModel] for feed provided by some [AbstractFeedUseCase] impl. Data exposed as [LiveData] via [feedLiveData]
 * property.
 */
abstract class AbstractPostFeedViewModel<out T : PostFeedUpdateRequest>(
    feedUseCase: AbstractFeedUseCase<out T, PostEntity, PostModel>,
    voteUseCase: VoteUseCase
) : AbstractFeedViewModel<T, PostEntity, PostModel>(
    feedUseCase,
    voteUseCase
) {
    companion object {
        const val PAGE_SIZE = 20
    }

    override fun vote(power: Short, discussionModel: PostModel) {
        if (!discussionModel.votes.hasUpVoteProgress
            && !discussionModel.votes.hasDownVotingProgress
            && !discussionModel.votes.hasVoteCancelProgress
        ) {
            val request = VoteRequestModel.VoteForPostRequest(power, discussionModel.contentId)
            voteUseCase.vote(request)
            handledVotes.remove(discussionModel.contentId)
        }
    }
}


