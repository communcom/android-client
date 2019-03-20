package io.golos.cyber_android.ui.screens.feed

import android.util.Log
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.model.PostFeed
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.map
import io.golos.domain.model.PostFeedUpdateRequest

abstract class AbstractFeedViewModel<T : PostFeedUpdateRequest>(private val feedUseCase: AbstractFeedUseCase<T>) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 20
    }
    var pageNum = 0L

    private val observer = Observer<Any> {}
    private val feedLiveData = feedUseCase.getAsLiveData.map(Function<PostFeed, List<PostModel>> {
        Log.i("PostsDataSource", "loaded $pageNum")
        if (pageNum == 0L) {
            initialCallback?.onResult(it.items, 0, 1)
            initialCallback = null
        }
        else {
            callbacks[pageNum]?.onResult(it.items, pageNum + 1)
            callbacks[pageNum] = null
        }
        it.items
    })

    val pagedListLiveData: LiveData<PagedList<PostModel>>

    private val dataSourceFactory = PostsDataSourceFactory(feedUseCase)

    private val callbacks = mutableMapOf<Long, PageKeyedDataSource.LoadCallback<Long, PostModel>?>()
    private var initialCallback: PageKeyedDataSource.LoadInitialCallback<Long, PostModel>? = null

    init {
        feedUseCase.subscribe()
        feedUseCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.REFRESH_FROM_BEGINNING)

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
        pageNum = 0
        dataSourceFactory.latestDataSource?.invalidate()
    }

    inner class PostsDataSourceFactory(private val communityFeedUserCase: AbstractFeedUseCase<T>): DataSource.Factory<Long, PostModel>() {

        var latestDataSource: DataSource<Long, PostModel>? = null

        override fun create(): DataSource<Long, PostModel> {
            latestDataSource = PostsDataSource(communityFeedUserCase)
            return latestDataSource!!
        }
    }

    inner class PostsDataSource(private val communityFeedUserCase: AbstractFeedUseCase<T>) : PageKeyedDataSource<Long, PostModel>() {

        override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, PostModel>) {
            communityFeedUserCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.REFRESH_FROM_BEGINNING)
            initialCallback = callback
            Log.i("PostsDataSource", "loadInitial")
        }

        override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, PostModel>) {
            communityFeedUserCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.FETCH_NEXT_PAGE)
            callbacks[params.key] = callback
            pageNum++
            Log.i("PostsDataSource", "loadAfter")
        }

        override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, PostModel>) {
        }

    }
}