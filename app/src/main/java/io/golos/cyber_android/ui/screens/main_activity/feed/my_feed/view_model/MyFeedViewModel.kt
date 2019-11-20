package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view_model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dialogs.post.model.PostMenu
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.mappers.mapToPostsList
import io.golos.cyber_android.ui.mappers.mapToTimeFrameDomain
import io.golos.cyber_android.ui.mappers.mapToTypeFeedDomain
import io.golos.cyber_android.ui.mappers.mapToUser
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.model.MyFeedModel
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.view_commands.*
import io.golos.cyber_android.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MyFeedViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: MyFeedModel,
    private val paginator: Paginator.Store<Post>
) : ViewModelBase<MyFeedModel>(dispatchersProvider, model), MyFeedListListener {

    override fun onDownVoteClicked() {

    }

    override fun onUpVoteClicked() {

    }

    override fun onLinkClicked(linkUri: Uri) {
        _command.value = NavigateToLinkViewCommand(linkUri)
    }

    override fun onImageClicked(imageUri: Uri) {
        _command.value = NavigateToImageViewCommand(imageUri)
    }

    override fun onUserClicked(userId: String) {
        _command.value = NavigateToUserProfileViewCommand(userId)
    }

    override fun onMenuClicked(postMenu: PostMenu) {
        _command.value = NavigationToPostMenuViewCommand(postMenu)
    }

    override fun onCommentsClicked(postContentId: Post.ContentId) {
        val discussionIdModel = DiscussionIdModel(postContentId.userId, Permlink(postContentId.permlink))
        _command.value = NavigateToPostCommand(discussionIdModel)
    }

    private val _postsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    val postsListState = _postsListState.toLiveData()

    private val _user: MutableLiveData<User> = MutableLiveData()

    val user = _user.toLiveData()

    private lateinit var postsConfigurationDomain: PostsConfigurationDomain

    private val _loadUserProgressVisibility: MutableLiveData<Boolean> = MutableLiveData(false)

    val loadUserProgressVisibility = _loadUserProgressVisibility.toLiveData()

    private val _loadUserErrorVisibility: MutableLiveData<Boolean> = MutableLiveData(false)

    val loadUserErrorVisibility = _loadUserErrorVisibility.toLiveData()

    private var loadPostsJob: Job? = null

    init {
        applyFiltersListener()

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
    }

    fun addToFavorite() {}

    fun removeFromFavorite() {}

    fun sharePost(shareUrl: String) {}

    fun editPost() {}

    fun deletePost() {}

    fun joinToCommunity(communityId: String) {}

    fun joinedToCommunity(communityId: String) {}

    fun reportPost() {}

    private fun applyFiltersListener(){
        launch {
            model.feedFiltersFlow.collect {
                if (::postsConfigurationDomain.isInitialized) {
                    val feedType = it.updateTimeFilter.mapToTypeFeedDomain()
                    val feedTimeFrame = it.periodTimeFilter.mapToTimeFrameDomain()
                    if(feedType != postsConfigurationDomain.typeFeed ||
                        feedTimeFrame != postsConfigurationDomain.timeFrame){
                        postsConfigurationDomain = postsConfigurationDomain.copy(
                            typeFeed = feedType,
                            timeFrame = feedTimeFrame
                        )
                        paginator.initState(Paginator.State.Empty)
                        restartLoadPosts()
                    }
                }
            }
        }
    }

    fun loadMorePosts() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    private fun loadMorePosts(pageCount: Int) {
        Timber.d("paginator: load posts on page -> $pageCount")
        loadPostsJob = launch {
            try {
                postsConfigurationDomain = postsConfigurationDomain.copy(offset = pageCount * PAGINATION_PAGE_SIZE)
                val postsDomainList = model.getPosts(postsConfigurationDomain)
                val postList = postsDomainList.mapToPostsList()
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
                paginator.proceed(Paginator.Action.PageError(e))
            }
        }
    }

    fun start() {
        if (_user.value == null) {
            loadLocalUser {
                if (it) {
                    loadInitialPosts()
                }
            }
        } else {
            loadInitialPosts()
        }
    }

    private fun restartLoadPosts(){
        loadPostsJob?.cancel()
        paginator.proceed(Paginator.Action.Restart)
    }

    private fun loadInitialPosts() {
        val postsListState = _postsListState.value
        if (postsListState is Paginator.State.Empty || postsListState is Paginator.State.EmptyError) {
            restartLoadPosts()
        }
    }

    private fun loadLocalUser(isUserLoad: (Boolean) -> Unit) {
        launch {
            try {
                _loadUserErrorVisibility.value = false
                _loadUserProgressVisibility.value = true
                val userProfile = model.getLocalUser().mapToUser()
                _user.value = userProfile
                val feedFilters = model.feedFiltersFlow.first()
                val feedType = feedFilters.updateTimeFilter.mapToTypeFeedDomain()
                val feedTimeFrame = feedFilters.periodTimeFilter.mapToTimeFrameDomain()
                postsConfigurationDomain = PostsConfigurationDomain(
                    userProfile.id,
                    null,
                    null,
                    PostsConfigurationDomain.SortByDomain.TIME,
                    feedTimeFrame,
                    PAGINATION_PAGE_SIZE,
                    0,
                    feedType
                )
                isUserLoad.invoke(true)
            } catch (e: Exception) {
                _loadUserErrorVisibility.value = true
                isUserLoad.invoke(false)
            } finally {
                _loadUserProgressVisibility.value = false
            }

        }
    }

    override fun onCleared() {
        loadPostsJob?.cancel()
        super.onCleared()
    }
}
