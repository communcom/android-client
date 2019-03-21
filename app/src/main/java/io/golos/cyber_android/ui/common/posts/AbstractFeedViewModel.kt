package io.golos.cyber_android.ui.common.posts

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.map
import io.golos.domain.model.PostFeedUpdateRequest

/**
 * Base [ViewModel] for feed provided by some [AbstractFeedUseCase] impl. Data exposed as [PagedList] via [pagedListLiveData]
 * property.
 */
abstract class AbstractFeedViewModel<out T : PostFeedUpdateRequest>(private val feedUseCase: AbstractFeedUseCase<out T>) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 20
    }

    /**
     * [LiveData] of [PagedList].
     */
    val pagedListLiveData: LiveData<PagedList<PostModel>>

    private var page = 0L
    private val observer = Observer<Any> {}
    private val feedLiveData = feedUseCase.getLastFetchedChunk.map(Function<List<PostModel>, List<PostModel>> {
        if (page == 0L) {
            initialCallback?.onResult(it, 0, 1)
            initialCallback = null
        }
        else {
            callbacks[page]?.onResult(it, page + 1)
            callbacks[page] = null
        }
        it
    })

    private val dataSourceFactory = PostsDataSourceFactory(feedUseCase)

    private val callbacks = mutableMapOf<Long, PageKeyedDataSource.LoadCallback<Long, PostModel>?>()
    private var initialCallback: PageKeyedDataSource.LoadInitialCallback<Long, PostModel>? = null

    init {
        feedUseCase.subscribe()

        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(1)
            .setPageSize(PAGE_SIZE)
            .build()

        pagedListLiveData = LivePagedListBuilder(dataSourceFactory, pagedListConfig)
            .build()

        feedLiveData.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        feedUseCase.unsubscribe()
        feedLiveData.removeObserver(observer)
    }

    fun requestRefresh() {
        feedUseCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.REFRESH_FROM_BEGINNING)
        page = 0
        dataSourceFactory.latestDataSource?.invalidate()
    }

    inner class PostsDataSourceFactory(private val communityFeedUserCase: AbstractFeedUseCase<out T>): DataSource.Factory<Long, PostModel>() {

        var latestDataSource: DataSource<Long, PostModel>? = null

        override fun create(): DataSource<Long, PostModel> {
            latestDataSource = PostsDataSource(communityFeedUserCase)
            return latestDataSource!!
        }
    }

    inner class PostsDataSource(private val communityFeedUserCase: AbstractFeedUseCase<out T>) : PageKeyedDataSource<Long, PostModel>() {

        override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, PostModel>) {
            communityFeedUserCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.REFRESH_FROM_BEGINNING)
            initialCallback = callback
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