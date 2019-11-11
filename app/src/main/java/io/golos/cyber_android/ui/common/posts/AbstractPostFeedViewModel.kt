package io.golos.cyber_android.ui.common.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.domain.dto.PostEntity
import io.golos.domain.use_cases.action.VoteUseCase
import io.golos.domain.use_cases.feed.AbstractFeedUseCase
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.publish.DiscussionPosterUseCase
import io.golos.domain.use_cases.sign.SignInUseCase
import io.golos.domain.requestmodel.PostFeedUpdateRequest

/**
 * Base [ViewModel] for feed provided by some [AbstractFeedUseCase] impl. Data exposed as [LiveData] via [feedLiveData]
 * property.
 */
abstract class AbstractPostFeedViewModel<out T : PostFeedUpdateRequest>(
    feedUseCase: AbstractFeedUseCase<out T, PostEntity, PostModel>,
    voteUseCase: VoteUseCase,
    posterUseCase: DiscussionPosterUseCase,
    signInUseCase: SignInUseCase
) : AbstractFeedWithCommentsViewModel<T, PostEntity, PostModel>(
    feedUseCase,
    voteUseCase,
    posterUseCase,
    signInUseCase
) {
    companion object {
        const val PAGE_SIZE = 20
    }

    override fun vote(power: Short, discussionModel: PostModel) {
//        if (!discussionModel.votes.hasUpVoteProgress
//            && !discussionModel.votes.hasDownVotingProgress
//            && !discussionModel.votes.hasVoteCancelProgress
//        ) {
//            val request = VoteRequestModel.VoteForPostRequest(power, discussionModel.contentId)
//            voteUseCase.vote(request)
//            pendingVotes.add(discussionModel.contentId)
//        }
    }
}