package io.golos.cyber_android.ui.common

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.Event
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.entities.DiscussionEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionModel
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.map
import io.golos.domain.model.FeedUpdateRequest
import io.golos.domain.model.QueryResult
import io.golos.domain.model.VoteRequestModel

/**
 * Base [ViewModel] for feed provided by some [AbstractFeedUseCase] impl. Data exposed as [LiveData] via [feedLiveData]
 * property.
 */
abstract class AbstractFeedViewModel<out R : FeedUpdateRequest, E : DiscussionEntity, M : DiscussionModel>(
    private val feedUseCase: AbstractFeedUseCase<out R, E, M>,
    protected val voteUseCase: VoteUseCase
) : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    protected val handledVotes = mutableSetOf<DiscussionIdModel>()

    /**
     * [LiveData] that indicates if user is able to vote
     */
    val voteReadinessLiveData = voteUseCase.getVotingRediness.asEvent()

    /**
     * [LiveData] that indicates if there was error in vote process
     */
    val voteErrorLiveData = MediatorLiveData<Event<DiscussionIdModel>>().apply {
        addSource(voteUseCase.getAsLiveData) { map ->
            map.forEach { (id, result) ->
                if (result is QueryResult.Error && !handledVotes.contains(id)) {
                    this.postValue(Event(id))
                }
                if (result !is QueryResult.Loading) {
                    handledVotes.add(id)
                }
            }
        }
    }

    /**
     * [LiveData] that indicates if data is loading
     */
    val loadingStatusLiveData = feedUseCase.feedUpdateState.map(Function<QueryResult<UpdateOption>, Boolean> {
        it is QueryResult.Loading
    })

    /**
     * [LiveData] that indicates if last page was reached
     */
    val lastPageLiveData = feedUseCase.getLastFetchedChunk.map(Function<List<M>, Boolean> {
        it.size != PAGE_SIZE
    })

    /**
     * [LiveData] of all the [DiscussionModel] items
     */
    val feedLiveData = feedUseCase.getAsLiveData.map(Function<DiscussionsFeed<M>, List<M>> {
        it.items
    })

    init {
        feedUseCase.subscribe()
        voteUseCase.subscribe()
        requestRefresh()
    }

    override fun onCleared() {
        super.onCleared()
        feedUseCase.unsubscribe()
        voteUseCase.unsubscribe()
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
        handledVotes.remove(discussionIdModel)
    }
}