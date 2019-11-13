package io.golos.cyber_android.ui.screens.my_feed.view_model

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.GetPostsConfiguration
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.mappers.PostDomainListToPostListMapper
import io.golos.cyber_android.ui.screens.my_feed.model.MyFeedModel
import io.golos.cyber_android.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.PostsConfigurationDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MyFeedViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: MyFeedModel,
    private val getPostsConfiguration: GetPostsConfiguration,
    private val paginator: Paginator.Store<Post>
) : ViewModelBase<MyFeedModel>(dispatchersProvider, model) {

    private val _postsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    val postsListState = _postsListState.toLiveData()

    private val _user: MutableLiveData<User> = MutableLiveData()

    val user = _user.toLiveData()

    private var postsConfigurationDomain: PostsConfigurationDomain

    init {

        paginator.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadMorePosts(it.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {

                }
            }
        }
        paginator.render = {
            Timber.d("paginator: render update -> [${it::class.java.simpleName.toUpperCase()}]")
            _postsListState.value = it
        }

        postsConfigurationDomain = PostsConfigurationDomain(
            getPostsConfiguration.userId,
            null,
            null,
            PostsConfigurationDomain.SortByDomain.TIME,
            PostsConfigurationDomain.TimeFrameDomain.DAY,
            PAGINATION_PAGE_SIZE,
            0,
            PostsConfigurationDomain.TypeFeedDomain.NEW
        )
    }

    fun loadMorePosts() {
        val postsListState = _postsListState.value
        if (postsListState is Paginator.State.Empty || postsListState is Paginator.State.EmptyError) {
            paginator.proceed(Paginator.Action.Restart)
        } else{
            paginator.proceed(Paginator.Action.LoadMore)
        }
    }

    private fun loadMorePosts(pageCount: Int) {
        Timber.d("paginator: load posts on page -> $pageCount")
        launch {
            try {
                postsConfigurationDomain = postsConfigurationDomain.copy(offset = pageCount * PAGINATION_PAGE_SIZE)
                val postsDomainList = model.getPosts(postsConfigurationDomain)
                val postList = PostDomainListToPostListMapper().invoke(postsDomainList)
                Timber.d("paginator: post list size -> ${postList.size}")
                launch(Dispatchers.Main) {
                    paginator.proceed(
                        Paginator.Action.NewPage(
                            pageCount,
                            postList
                        )
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun start() {
        loadMorePosts()
        /*if(_user.value == null){

        } else{

        }*/
    }
}
