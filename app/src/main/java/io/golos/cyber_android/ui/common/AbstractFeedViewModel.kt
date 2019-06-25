package io.golos.cyber_android.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.utils.combinedWith
import io.golos.domain.entities.DiscussionEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.map
import io.golos.domain.requestmodel.FeedUpdateRequest
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.VoteRequestModel

/**
 * Base [ViewModel] for feed provided by some [AbstractFeedUseCase] impl. Data exposed as [LiveData] via [feedLiveData]
 * property.
 */
abstract class AbstractFeedViewModel<out R : FeedUpdateRequest, E : DiscussionEntity, M : DiscussionModel>(
    private val feedUseCase: AbstractFeedUseCase<out R, E, M>,
    protected val voteUseCase: VoteUseCase,
    private val signInUseCase: SignInUseCase,
    private val posterUseCase: DiscussionPosterUseCase
) : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    /**
     * [LiveData] for post creation process
     */
    val discussionCreationLiveData = posterUseCase.getAsLiveData.asEvent()

    /**
     * Set of votes this ViewModel should handle.
     * When [vote] method is invoked we add id of a discussion to vote into this set.
     * After that in [voteErrorLiveData] we remove ids of requests that are completed (eg not [QueryResult.Loading]).
     * This way we can handle all vote errors associated with this ViewModel.
     */
    protected val pendingVotes = mutableSetOf<DiscussionIdModel>()

    /**
     * [LiveData] that indicates if user is able to vote
     */
    val voteReadinessLiveData = voteUseCase.getVotingReadiness.asEvent()

    private val voteErrorLiveData = MediatorLiveData<Pair<DiscussionIdModel, QueryResult.Error<VoteRequestModel>>>().apply {
        addSource(voteUseCase.getAsLiveData) { map ->
            map.forEach { (id, result) ->
                if (result is QueryResult.Error && pendingVotes.contains(id)) {
                    this.postValue(id to result)
                }
                if (result !is QueryResult.Loading) {
                    pendingVotes.remove(id)
                }
            }
        }
    }

    /**
     * [LiveData] that indicates if there was error in vote process
     */
    val getVoteErrorLiveData = voteErrorLiveData.asEvent()

    /**
     * [LiveData] that indicates if data is loading
     */
    val loadingStatusLiveData = feedUseCase.feedUpdateState.map {
        it is QueryResult.Loading
    }

    /**
     * [LiveData] that indicates if last page was reached
     */
    val lastPageLiveData = feedUseCase.getLastFetchedChunk.map {
        it?.size != PAGE_SIZE || it.isEmpty()
    }

    /**
     * [LiveData] of all the [DiscussionModel] items
     */
    val feedLiveData = feedUseCase.getAsLiveData.map {
        it?.items
    }.combinedWith(signInUseCase.getAsLiveData) { feed, authState ->
        feed?.forEach { it.isActiveUserDiscussion = it.contentId.userId == authState?.userName?.name }
        feed ?: emptyList()
    }

    init {
        feedUseCase.subscribe()
        voteUseCase.subscribe()
        signInUseCase.subscribe()
        posterUseCase.subscribe()
        requestRefresh()
    }

    override fun onCleared() {
        super.onCleared()
        feedUseCase.unsubscribe()
        voteUseCase.unsubscribe()
        signInUseCase.unsubscribe()
        posterUseCase.unsubscribe()
    }

    fun requestRefresh() {
        feedUseCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.REFRESH_FROM_BEGINNING)
    }

    fun loadMore() {
        feedUseCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.FETCH_NEXT_PAGE)
    }

    fun onUpvote(post: M) {
        val power = if (!post.votes.hasUpVote) 10_000.toShort() else 0.toShort()
        vote(power, post)
    }

    fun onDownvote(post: M) {
        val power = if (!post.votes.hasDownVote) (-10_000).toShort() else 0.toShort()
        vote(power, post)
    }

    /**
     * Make vote. This needs to be overriden to provide correct [VoteRequestModel].
     * After that this request can be used in [vote] function
     */
    abstract fun vote(power: Short, discussionModel: M)

    /**
     * Make vote
     * @param request vote request
     * @param discussionIdModel id of entity to vote
     */
    protected fun vote(
        request: VoteRequestModel,
        discussionIdModel: DiscussionIdModel
    ) {
        voteUseCase.vote(request)
        pendingVotes.add(discussionIdModel)
    }

    fun deleteDiscussion(contentId: DiscussionIdModel) {
        posterUseCase.deletePostOrComment(contentId)
    }
}