package io.golos.cyber_android.ui.screens.postslist

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.PostsConfigurationDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostsListViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostsListModel,
    private val getPostsConfiguration: GetPostsConfiguration,
    private val paginator: Paginator.Store<Post>
) : ViewModelBase<PostsListModel>(dispatchersProvider, model) {

    private val _followersListStateLiveData: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

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
            _followersListStateLiveData.value = it
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

    private fun loadMorePosts(pageCount: Int){
        launch {
            postsConfigurationDomain = postsConfigurationDomain.copy(offset = pageCount * PAGE_SIZE)
            model.getPosts(postsConfigurationDomain)
        }
    }

    fun start() {
        val followersListState = _followersListStateLiveData.value
        if (followersListState is Paginator.State.Empty || followersListState is Paginator.State.EmptyError) {
            paginator.proceed(Paginator.Action.Restart)
        }
    }

    private companion object{
        private const val PAGE_SIZE = 15
    }
}
