package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view_model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.mappers.mapToPostsList
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.model.MyFeedModel
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.view_commands.NavigateToImageViewCommand
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.view_commands.NavigateToLinkViewCommand
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.view_commands.NavigateToPostCommand
import io.golos.cyber_android.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MyFeedViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: MyFeedModel,
    private val paginator: Paginator.Store<Post>
) : ViewModelBase<MyFeedModel>(dispatchersProvider, model), MyFeedViewModelListEventsProcessor {

    override fun onLinkInPostClick(link: Uri) {
        _command.value = NavigateToLinkViewCommand(link)
    }

    override fun onImageInPostClick(imageUri: Uri) {
        _command.value = NavigateToImageViewCommand(imageUri)
    }

    override fun onUserInPostClick(userName: String) {

    }

    override fun onUpVoteClick() {

    }

    override fun onDownVoteClick() {

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
    }

    fun loadMorePosts() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    private fun loadMorePosts(pageCount: Int) {
        Timber.d("paginator: load posts on page -> $pageCount")
        launch {
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
        if(_user.value == null){
            loadLocalUser{
                if(it){
                    loadInitialPosts()
                }
            }
        } else{
            loadInitialPosts()
        }
    }

    private fun loadInitialPosts(){
        val postsListState = _postsListState.value
        if (postsListState is Paginator.State.Empty || postsListState is Paginator.State.EmptyError) {
            paginator.proceed(Paginator.Action.Restart)
        }
    }

    private fun loadLocalUser(isUserLoad: (Boolean) -> Unit){
        launch {
            try {
                _loadUserErrorVisibility.value = false
                _loadUserProgressVisibility.value = true
                //val userProfile = UserDomainMapper().invoke(model.getLocalUser())
                val userProfile = User("1", "sdsds", "")
                _user.value = userProfile
                postsConfigurationDomain = PostsConfigurationDomain(
                    userProfile.id,
                    null,
                    null,
                    PostsConfigurationDomain.SortByDomain.TIME,
                    PostsConfigurationDomain.TimeFrameDomain.DAY,
                    PAGINATION_PAGE_SIZE,
                    0,
                    PostsConfigurationDomain.TypeFeedDomain.NEW
                )
                isUserLoad.invoke(true)
            } catch (e: Exception){
                _loadUserErrorVisibility.value = true
                isUserLoad.invoke(false)
            } finally {
                _loadUserProgressVisibility.value = false
            }

        }
    }
}
