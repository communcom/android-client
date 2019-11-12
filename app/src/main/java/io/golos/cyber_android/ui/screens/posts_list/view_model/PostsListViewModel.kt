package io.golos.cyber_android.ui.screens.posts_list.view_model

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.GetPostsConfiguration
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.mappers.PostDomainListToPostListMapper
import io.golos.cyber_android.ui.screens.posts_list.model.PostsListModel
import io.golos.cyber_android.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.PostsConfigurationDomain
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class PostsListViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostsListModel,
    private val getPostsConfiguration: GetPostsConfiguration,
    private val paginator: Paginator.Store<Post>
) : ViewModelBase<PostsListModel>(dispatchersProvider, model) {

    private val _postsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    val postsListState = _postsListState.toLiveData()

    private var postsConfigurationDomain: PostsConfigurationDomain

    init {
        paginator.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadPosts(it.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {

                }
            }
        }
        paginator.render = {
            _postsListState.value = it
        }

        postsConfigurationDomain = PostsConfigurationDomain(getPostsConfiguration.userId,
            null,
            null,
            PostsConfigurationDomain.SortByDomain.TIME,
            PostsConfigurationDomain.TimeFrameDomain.DAY,
            PAGE_SIZE,
            0,
            PostsConfigurationDomain.TypeFeedDomain.NEW)
    }

    fun loadMorePosts(){
        paginator.proceed(Paginator.Action.LoadMore)

    }

    private fun loadPosts(pageCount: Int){
        launch {
            try {
                postsConfigurationDomain = postsConfigurationDomain.copy(offset = pageCount * PAGE_SIZE)
                val postsDomainList = model.getPosts(postsConfigurationDomain)
                val postList = PostDomainListToPostListMapper().invoke(postsDomainList)
                paginator.proceed(
                    Paginator.Action.NewPage(
                        pageCount,
                        postList
                    )
                )
            } catch (e: Exception){
                Timber.e(e)
            }
        }
    }

    fun start() {
        val followersListState = _postsListState.value
        if (followersListState is Paginator.State.Empty || followersListState is Paginator.State.EmptyError) {
            paginator.proceed(Paginator.Action.Restart)
        }
    }

    private companion object{
        private const val PAGE_SIZE = 15
    }
}
