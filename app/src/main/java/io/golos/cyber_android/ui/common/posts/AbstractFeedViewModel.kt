package io.golos.cyber_android.ui.common.posts

import android.util.Log
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
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
 * Base [ViewModel] for feed provided by some [AbstractFeedUseCase] impl. Data exposed as [PagedList] via [pagedListLiveData]
 * property.
 */
abstract class AbstractFeedViewModel<out T : PostFeedUpdateRequest>(
    private val feedUseCase: AbstractFeedUseCase<out T>,
    private val voteUseCase: VoteUseCase
) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 20
    }

    /**
     * [LiveData] of [PagedList].
     */
    val pagedListLiveData: LiveData<PagedList<PostModel>>

    /**
     * [LiveData] that indicates if user is able to vote
     */
    val voteReadinessLiveData = voteUseCase.getVotingRediness.asEvent()

    /**
     * [LiveData] that indicates if there was error in vote process
     */
    val voteErrorLiveData = MutableLiveData<Event<Any>>()

    private var page = 0L
    private var pendingVotesRequestCount = 0
    private val observer = Observer<Any> {}
    private val pendingVotes = mutableListOf<DiscussionIdModel>()

    private val feedLiveData = feedUseCase.getLastFetchedChunk.map(Function<List<PostModel>, Any> {
        if (page == 0L) {
            initialCallback?.onResult(it, 0, 1)
            initialCallback = null
        } else {
            callbacks[page]?.onResult(it, page + 1)
            callbacks[page] = null
        }
    })

    private val allFeedLiveData = feedUseCase.getAsLiveData.map(Function<PostFeed, Any> {
        if (pendingVotesRequestCount > 0) {
            pendingVotesRequestCount--
            dataSourceFactory.latestDataSource?.invalidate()
        }
    })

    private val votesLiveData = voteUseCase
        .getAsLiveData
        .map(Function<Map<DiscussionIdModel, QueryResult<VoteRequestModel>>, Any> { map ->
            dataSourceFactory.latestDataSource?.invalidate()

            pendingVotes.forEach { key ->
                if (map.filter { it.value is QueryResult.Success<*> || it.value is QueryResult.Error<*> }.containsKey(key)) {
                    pendingVotesRequestCount++
                    pendingVotes.remove(key)
                }
            }
        })


    private val dataSourceFactory = PostsDataSourceFactory(feedUseCase)

    private val callbacks = mutableMapOf<Long, PageKeyedDataSource.LoadCallback<Long, PostModel>?>()
    private var initialCallback: PageKeyedDataSource.LoadInitialCallback<Long, PostModel>? = null

    init {
        feedUseCase.subscribe()
        voteUseCase.subscribe()

        pagedListLiveData = createFeedPagedList()

        feedLiveData.observeForever(observer)
        allFeedLiveData.observeForever(observer)
        votesLiveData.observeForever(observer)
    }

    private fun createFeedPagedList(): LiveData<PagedList<PostModel>> {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPageSize(PAGE_SIZE)
            .build()

        return LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
    }

    override fun onCleared() {
        super.onCleared()
        feedUseCase.unsubscribe()
        voteUseCase.unsubscribe()

        feedLiveData.removeObserver(observer)
        allFeedLiveData.removeObserver(observer)
        votesLiveData.removeObserver(observer)
    }

    fun requestRefresh() {
        page = 0
        dataSourceFactory.latestDataSource?.invalidate()
    }


    fun onVote(post: PostModel, power: Short) {
        val request = VoteRequestModel.VoteForPostRequest(power, post.contentId)
        voteUseCase.vote(request)
        pendingVotes.add(post.contentId)
        pendingVotesRequestCount++
    }

    inner class PostsDataSourceFactory(private val communityFeedUserCase: AbstractFeedUseCase<out T>) :
        DataSource.Factory<Long, PostModel>() {

        var latestDataSource: DataSource<Long, PostModel>? = null

        override fun create(): DataSource<Long, PostModel> {
            latestDataSource = PostsDataSource(communityFeedUserCase)
            return latestDataSource!!
        }
    }

    inner class PostsDataSource(private val communityFeedUserCase: AbstractFeedUseCase<out T>) :
        PageKeyedDataSource<Long, PostModel>() {

        override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, PostModel>) {
            val postFeed = communityFeedUserCase.getAsLiveData.value
            if (postFeed?.items.isNullOrEmpty() || page == 0L) {
                communityFeedUserCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.REFRESH_FROM_BEGINNING)
                initialCallback = callback
            } else {
                page = (postFeed!!.items.size / PAGE_SIZE).toLong()
                if (postFeed.items.size % PAGE_SIZE == 0) page -= 1L
                callback.onResult(postFeed.items, 0, page + 1)
            }
        }

        override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, PostModel>) {
            communityFeedUserCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.FETCH_NEXT_PAGE)
            callbacks[params.key] = callback
            page++
        }

        override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, PostModel>) {
        }
    }
}


