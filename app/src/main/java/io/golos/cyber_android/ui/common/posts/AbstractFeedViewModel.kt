package io.golos.cyber_android.ui.common.posts

import android.util.Log
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PageKeyedDataSource
import io.golos.cyber_android.utils.Event
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostFeed
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.map
import io.golos.domain.model.PostFeedUpdateRequest
import io.golos.domain.model.QueryResult
import io.golos.domain.model.VoteRequestModel

/**
 * Base [ViewModel] for feed provided by some [AbstractFeedUseCase] impl. Data exposed as [LiveData] via [feedLiveData]
 * property.
 */
abstract class AbstractFeedViewModel<out T : PostFeedUpdateRequest>(
    private val feedUseCase: AbstractFeedUseCase<out T>,
    private val voteUseCase: VoteUseCase
) : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    /**
     * [LiveData] that indicates if user is able to vote
     */
    val voteReadinessLiveData = voteUseCase.getVotingRediness.asEvent()

    /**
     * [LiveData] that indicates if there was error in vote process
     */
    val voteErrorLiveData = MutableLiveData<Event<Any>>()

    /**
     * [LiveData] that indicates if data is loading
     */
    val loadingStatusLiveData = feedUseCase.feedUpdateState.map(Function<QueryResult<UpdateOption>, Boolean> {
        it is QueryResult.Loading
    })

    /**
     * [LiveData] that indicates if last page was reached
     */
    val lastPageLiveData = feedUseCase.getLastFetchedChunk.map(Function<List<PostModel>, Boolean> {
        it.size != PAGE_SIZE
    })

    /**
     * [LiveData] of all the [PostModel] items
     */
    val feedLiveData = feedUseCase.getAsLiveData.map(Function<PostFeed, List<PostModel>> {
        it.items
    })

    private val pendingVotes = mutableListOf<DiscussionIdModel>()

    private val callbacks = mutableMapOf<Long, PageKeyedDataSource.LoadCallback<Long, PostModel>?>()
    private var initialCallback: PageKeyedDataSource.LoadInitialCallback<Long, PostModel>? = null

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

    fun onUpvote(post: PostModel) {
        val power = if (!post.votes.hasUpVote) 10_000.toShort() else 0.toShort()
        val request = VoteRequestModel.VoteForPostRequest(power, post.contentId)
        voteUseCase.vote(request)
    }

    fun onDownvote(post: PostModel) {
        val power = if (!post.votes.hasDownVote) (-10_000).toShort() else 0.toShort()
        val request = VoteRequestModel.VoteForPostRequest(power, post.contentId)
        voteUseCase.vote(request)
    }
}


