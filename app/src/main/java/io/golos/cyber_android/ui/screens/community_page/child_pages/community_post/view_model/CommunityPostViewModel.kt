package io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.view_model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.mappers.mapToPostsList
import io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.model.CommunityPostModel
import io.golos.cyber_android.ui.screens.my_feed.view_model.MyFeedListListener
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.ui.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class CommunityPostViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPostModel,
    currentUserRepository: CurrentUserRepositoryRead,
    @Named(Clarification.COMMUNITY_ID) communityId: String,
    private val paginator: Paginator.Store<Post>
) : ViewModelBase<CommunityPostModel>(dispatchersProvider, model), MyFeedListListener {

    private val _postsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)
    val postsListState = _postsListState.toLiveData()

    private var loadPostsJob: Job? = null
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
            _postsListState.value = it
        }

        postsConfigurationDomain = PostsConfigurationDomain(
            currentUserRepository.userId.userId,
            communityId,
            null,
            PostsConfigurationDomain.SortByDomain.TIME_DESC,
            PostsConfigurationDomain.TimeFrameDomain.DAY,
            PAGINATION_PAGE_SIZE,
            0,
            PostsConfigurationDomain.TypeFeedDomain.COMMUNITY
        )

        loadInitialPosts()
    }

    override fun onLinkClicked(linkUri: Uri) {
    }

    override fun onImageClicked(imageUri: Uri) {
    }

    override fun onItemClicked(contentId: ContentId) {
    }

    override fun onUserClicked(userId: String) {
    }

    override fun onMenuClicked(postMenu: PostMenu) {
    }

    override fun onSeeMoreClicked(contentId: ContentId) {
    }

    override fun onCommentsClicked(postContentId: ContentId) {
    }

    override fun onShareClicked(shareUrl: String) {
    }

    override fun onUpVoteClicked(contentId: ContentId) {
    }

    override fun onDownVoteClicked(contentId: ContentId) {
    }

    fun loadInitialPosts() {
        val postsListState = _postsListState.value
        if (postsListState is Paginator.State.Empty || postsListState is Paginator.State.EmptyError) {
            restartLoadPosts()
        }
    }

    fun loadMorePosts() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    private fun loadMorePosts(pageCount: Int) {
        loadPostsJob = launch {
            try {
                postsConfigurationDomain = postsConfigurationDomain.copy(offset = pageCount * PAGINATION_PAGE_SIZE)
                val postsDomainList = model.getPosts(postsConfigurationDomain)
                val postList = postsDomainList.mapToPostsList()
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
                paginator.proceed(Paginator.Action.PageError(e))
            }
        }
    }

    private fun restartLoadPosts() {
        loadPostsJob?.cancel()
        paginator.proceed(Paginator.Action.Restart)
    }

    override fun onCleared() {
        loadPostsJob?.cancel()
        super.onCleared()
    }
}