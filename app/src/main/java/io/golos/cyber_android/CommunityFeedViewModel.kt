package io.golos.cyber_android

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import io.golos.domain.interactors.CommunityFeedUseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.map

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */


class CommunityFeedViewModel(private val communityFeedUserCase: CommunityFeedUseCase) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    val feedLiveData = communityFeedUserCase.getAsLiveData.map(Function<PostFeed, List<PostModel>> { it.items })


    init {
        communityFeedUserCase.subscribe()
        communityFeedUserCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.REFRESH_FROM_BEGINNING)

//        val pagedListConfig = PagedList.Config.Builder()
//            .setEnablePlaceholders(false)
//            .setInitialLoadSizeHint(1)
//            .setPageSize(20)
//            .build()
//
//        pagedListLiveData = LivePagedListBuilder(feedDataSource, pagedListConfig).build()

    }


    override fun onCleared() {
        super.onCleared()
        communityFeedUserCase.unsubscribe()
    }

    fun onSearch(query: String) {

    }

    fun requestRefresh() {
        communityFeedUserCase.requestFeedUpdate(PAGE_SIZE, UpdateOption.REFRESH_FROM_BEGINNING)
    }

//    inner class PostsDataSourceFactory(private val communityFeedUserCase: CommunityFeedUseCase): DataSource.Factory<Long, PostModel>() {
//        override fun create(): DataSource<Long, PostModel> {
//            return PostsDataSource(communityFeedUserCase)
//        }
//    }
//
//    inner class PostsDataSource(private val communityFeedUserCase: CommunityFeedUseCase) : PageKeyedDataSource<Long, PostModel>() {
//
//        override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, PostModel>) {
//            communityFeedUserCase.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)
//            feedLiveData.value?.let {  callback.onResult(feedLiveData.value!!.items, 0, 1) }
//
//        }
//
//        override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, PostModel>) {
//            communityFeedUserCase.requestFeedUpdate(20, UpdateOption.FETCH_NEXT_PAGE)
//            feedLiveData.value?.let {  callback.onResult(feedLiveData.value!!.items, params.key + 1) }
//        }
//
//        override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, PostModel>) {
//        }
//
//    }
}