package io.golos.cyber_android.ui.screens.profile_posts.view_model

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.DonateType
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.mappers.mapToPost
import io.golos.cyber_android.ui.mappers.mapToPostsList
import io.golos.cyber_android.ui.mappers.mapToUser
import io.golos.cyber_android.ui.screens.feed_my.model.MyFeedModel
import io.golos.cyber_android.ui.screens.feed_my.view_model.MyFeedListListener
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.profile_posts.view_commands.EditPostCommand
import io.golos.cyber_android.ui.screens.profile_posts.view_commands.NavigationToPostMenuViewCommand
import io.golos.cyber_android.ui.screens.profile_posts.view_commands.ReportPostCommand
import io.golos.cyber_android.ui.screens.profile_posts.view_commands.SharePostCommand
import io.golos.cyber_android.ui.shared.broadcast_actions_registries.PostCreateEditRegistry
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.post_view.RecordPostViewManager
import io.golos.cyber_android.ui.shared.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.*
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.use_cases.reward.isTopReward
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject

class ProfilePostsViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: MyFeedModel,
    private val profileUserId: UserIdDomain,
    private val paginator: Paginator.Store<Post>,
    private val startFeedType: PostsConfigurationDomain.TypeFeedDomain,
    private val postCreateEditRegistry: PostCreateEditRegistry,
    private val currentUserRepository: CurrentUserRepositoryRead,
    val recordPostViewManager: RecordPostViewManager
) : ViewModelBase<MyFeedModel>(dispatchersProvider, model), MyFeedListListener {

    private val _postsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)
    val postsListState = _postsListState.toLiveData()

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user = _user.toLiveData()

    private lateinit var postsConfigurationDomain: PostsConfigurationDomain

    private val _loadUserProgressVisibility: MutableLiveData<Boolean> = MutableLiveData(false)

    val loadUserProgressVisibility = _loadUserProgressVisibility.toLiveData()

    private val _loadUserErrorVisibility: MutableLiveData<Boolean> = MutableLiveData(false)

    val loadUserErrorVisibility = _loadUserErrorVisibility.toLiveData()

    val noDataStubText = MutableLiveData<Int>(R.string.no_posts).toLiveData()
    val noDataStubExplanation = MutableLiveData<Int>(R.string.no_posts_not_found).toLiveData()

    private val _noDataStubVisibility = MutableLiveData<Int>(View.GONE)
    val noDataStubVisibility: LiveData<Int> get() = _noDataStubVisibility

    private var loadPostsJob: Job? = null

    init {
        paginator.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadMorePosts(it.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {

                }
            }
        }
        paginator.render = { newState, oldState ->
            _postsListState.value = newState
            _noDataStubVisibility.value = if (newState == Paginator.State.Empty && oldState == Paginator.State.EmptyProgress) {
                View.VISIBLE
            }
            else {
                View.GONE
            }
        }

        applyPostCreatedChangeListener()
        applyPostUpdatedChangeListener()
    }

    override fun onShareClicked(shareUrl: String) {
        _command.value = SharePostCommand(shareUrl)
    }

    override fun onUpVoteClicked(contentId: ContentIdDomain) {
        launch {
            try {
                _postsListState.value = updateUpVoteCountOfVotes(_postsListState.value, contentId)
                model.upVote(contentId.communityId, contentId.userId, contentId.permlink)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
            }
        }
    }

    override fun onDownVoteClicked(contentId: ContentIdDomain) {
        launch {
            try {
                _postsListState.value = updateDownVoteCountOfVotes(_postsListState.value, contentId)
                model.downVote(contentId.communityId, contentId.userId, contentId.permlink)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
            }
        }
    }

    override fun onDonateClick(donate: DonateType, post: Post) {
        launch {
            _command.value = NavigateToDonateCommand.build(donate, post, model.getWalletBalance())
        }
    }

    override fun onLinkClicked(linkUri: Uri) {
        _command.value = NavigateToLinkViewCommand(linkUri)
    }

    override fun onImageClicked(imageUri: Uri) {
        _command.value = NavigateToImageViewCommand(imageUri)
    }

    override fun onUserClicked(userId: String) {
        _command.value = if(profileUserId.userId != userId) {
            NavigateToUserProfileCommand(UserIdDomain(userId))
        } else {
            ScrollProfileToTopCommand()
        }
    }

    override fun onCommunityClicked(communityId: CommunityIdDomain) {
        _command.value = NavigateToCommunityPageCommand(communityId)
    }

    override fun onSeeMoreClicked(contentId: ContentIdDomain): Boolean {
        val discussionIdModel = DiscussionIdModel(contentId.userId.userId, Permlink(contentId.permlink))
        _command.value = NavigateToPostCommand(discussionIdModel, contentId)
        return true
    }

    override fun onItemClicked(contentId: ContentIdDomain) {
        val discussionIdModel = DiscussionIdModel(contentId.userId.userId, Permlink(contentId.permlink))
        _command.value = NavigateToPostCommand(discussionIdModel, contentId)
    }

    override fun onMenuClicked(postMenu: PostMenu) {
        _command.value = NavigationToPostMenuViewCommand(postMenu)
    }

    override fun onRewardClick(reward: RewardPostDomain?) {
        reward.isTopReward()?.let {
            val title = if(it) R.string.post_reward_top_title else R.string.post_reward_not_top_title
            val text = if(it) R.string.post_reward_top_text else R.string.post_reward_not_top_text
            _command.value = ShowPostRewardDialogCommand(title, text)
        }
    }

    override fun onCommentsClicked(postContentId: ContentIdDomain) {
        openPost(postContentId)
    }

    override fun onBodyClicked(postContentId: ContentIdDomain?) {
        openPost(postContentId)
    }

    private fun openPost(postContentId: ContentIdDomain?){
        postContentId?.let {
            val discussionIdModel = DiscussionIdModel(it.userId.userId, Permlink(it.permlink))
            _command.value = NavigateToPostCommand(discussionIdModel, it)
        }
    }

    fun addToFavorite(permlink: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.addToFavorite(permlink)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun removeFromFavorite(permlink: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.removeFromFavorite(permlink)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.unknown_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }


    fun editPost(permlink: String) {
        val post = getPostFromPostsListState(permlink)
        post?.let {
            _command.value = EditPostCommand(it)
        }
    }

    fun deletePost(permlink: String, communityId: CommunityIdDomain) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.deletePost(permlink, communityId)
                _postsListState.value = deletePostInState(_postsListState.value, permlink)
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun deleteLocalPostByPermlink(permlink: String) {
        _postsListState.value = deletePostInState(_postsListState.value, permlink)
    }

    fun subscribeToCommunity(communityId: CommunityIdDomain) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.subscribeToCommunity(communityId)
                _postsListState.value = changeCommunitySubscriptionStatusInState(_postsListState.value, communityId, true)
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun unsubscribeToCommunity(communityId: CommunityIdDomain) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.unsubscribeToCommunity(communityId)
                _postsListState.value = changeCommunitySubscriptionStatusInState(_postsListState.value, communityId, false)
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun onSendReportClicked(permlink: String) {
        val post = getPostFromPostsListState(permlink)
        post?.let {
            _command.value = ReportPostCommand(post)
        }
    }

    private fun applyPostCreatedChangeListener() {
        launch {
            postCreateEditRegistry.createdPosts.collect {
                if(currentUserRepository.userId == profileUserId) {
                    it?.let {
                        paginator.initState(Paginator.State.Empty)
                        restartLoadPosts()
                    }
                }
            }
        }
    }

    private fun applyPostUpdatedChangeListener() {
        launch {
            postCreateEditRegistry.updatedPosts.collect {
                if(currentUserRepository.userId == profileUserId) {
                    it?.let {
                        paginator.proceed(Paginator.Action.UpdateItem(it.mapToPost()))
                    }
                }
            }
        }
    }

    private fun changeCommunitySubscriptionStatusInState(
        state: Paginator.State?,
        communityId: CommunityIdDomain,
        isSubscribed: Boolean
    ): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                val post = (state as? List<Post>)?.find { it.community.communityId == communityId }
                post?.community?.isSubscribed = isSubscribed
            }
            is Paginator.State.Refresh<*> -> {
                val post = (state as? List<Post>)?.find { it.community.communityId == communityId }
                post?.community?.isSubscribed = isSubscribed

            }
            is Paginator.State.NewPageProgress<*> -> {
                val post = (state as? List<Post>)?.find { it.community.communityId == communityId }
                post?.community?.isSubscribed = isSubscribed

            }
            is Paginator.State.FullData<*> -> {
                val post = (state as? List<Post>)?.find { it.community.communityId == communityId }
                post?.community?.isSubscribed = isSubscribed
            }
        }
        return state
    }

    private fun deletePostInState(state: Paginator.State?, permlink: String): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                deletePostByPermlink(((state).data as ArrayList<Post>), permlink)
            }
            is Paginator.State.Refresh<*> -> {
                deletePostByPermlink(((state).data as ArrayList<Post>), permlink)
            }
            is Paginator.State.NewPageProgress<*> -> {
                deletePostByPermlink(((state).data as ArrayList<Post>), permlink)
            }
            is Paginator.State.FullData<*> -> {
                deletePostByPermlink(((state).data as ArrayList<Post>), permlink)
            }
        }
        return state
    }

    private fun deletePostByPermlink(posts: ArrayList<Post>, permlink: String) {
        val foundedPost = posts.find { post ->
            post.contentId.permlink == permlink
        }?.copy()
        foundedPost?.let { post ->
            posts.remove(post)
        }
    }

    private fun getPostFromPostsListState(permlink: String): Post? {
        when (postsListState.value) {
            is Paginator.State.Data<*> -> {
                return ((postsListState.value as Paginator.State.Data<*>).data as List<Post>).find { post ->
                    post.contentId.permlink == permlink
                }
            }
            is Paginator.State.Refresh<*> -> {
                return ((postsListState.value as Paginator.State.Refresh<*>).data as List<Post>).find { post ->
                    post.contentId.permlink == permlink
                }
            }
            is Paginator.State.NewPageProgress<*> -> {
                return ((postsListState.value as Paginator.State.NewPageProgress<*>).data as List<Post>)
                    .find { post ->
                        post.contentId.permlink == permlink
                    }
            }
            is Paginator.State.FullData<*> -> {
                return ((postsListState.value as Paginator.State.FullData<*>).data as List<Post>).find { post ->
                    post.contentId.permlink == permlink
                }
            }
        }
        return null
    }

    fun loadMorePosts() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    private fun loadMorePosts(pageCount: Int) {
        loadPostsJob = launch {
            try {
                postsConfigurationDomain = postsConfigurationDomain.copy(offset = pageCount * PAGINATION_PAGE_SIZE)
                val postsDomainList = model.getPosts(postsConfigurationDomain, TypeObjectDomain.USER)
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

    private fun restartLoadPosts() {
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
                postsConfigurationDomain = PostsConfigurationDomain(
                    userId = profileUserId.userId,
                    communityId = null,
                    limit = PAGINATION_PAGE_SIZE,
                    offset = 0,
                    sortBy = PostsConfigurationDomain.SortByDomain.TIME_DESC,
                    timeFrame = PostsConfigurationDomain.TimeFrameDomain.ALL,
                    typeFeed = startFeedType
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

    private fun updateUpVoteCountOfVotes(
        state: Paginator.State?,
        contentId: ContentIdDomain
    ): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                updateUpVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)
            }
            is Paginator.State.Refresh<*> -> {
                updateUpVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)
            }
            is Paginator.State.NewPageProgress<*> -> {
                updateUpVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)

            }
            is Paginator.State.FullData<*> -> {
                updateUpVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)
            }
        }
        return state
    }

    private fun updateUpVoteInPostsByContentId(posts: ArrayList<Post>, contentId: ContentIdDomain){
        val foundedPost = posts.find { post ->
            post.contentId == contentId
        }
        val updatedPost = foundedPost?.copy()
        updatedPost?.let { post ->
            if (!post.votes.hasUpVote) {
                val oldVotes = post.votes
                post.votes = post.votes.copy(
                    upCount = post.votes.upCount + 1,
                    downCount = if (oldVotes.hasDownVote) oldVotes.downCount - 1 else oldVotes.downCount,
                    hasUpVote = true,
                    hasDownVote = false
                )
            }
            posts[posts.indexOf(foundedPost)] = updatedPost
        }
    }

    private fun updateDownVoteCountOfVotes(
        state: Paginator.State?,
        contentId: ContentIdDomain
    ): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                updateDownVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)
            }
            is Paginator.State.Refresh<*> -> {
                updateDownVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)

            }
            is Paginator.State.NewPageProgress<*> -> {
                updateDownVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)

            }
            is Paginator.State.FullData<*> -> {
                updateDownVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)
            }
        }
        return state
    }

    private fun updateDownVoteInPostsByContentId(posts: ArrayList<Post>, contentId: ContentIdDomain) {
        val foundedPost = posts.find { post ->
            post.contentId == contentId
        }
        val updatedPost = foundedPost?.copy()
        updatedPost?.let { post ->
            if (!post.votes.hasDownVote) {
                val oldVotes = post.votes
                post.votes = post.votes.copy(
                    downCount = post.votes.downCount + 1,
                    upCount = if (oldVotes.hasUpVote) oldVotes.upCount - 1 else oldVotes.upCount,
                    hasUpVote = false,
                    hasDownVote = true
                )
                posts[posts.indexOf(foundedPost)] = updatedPost
            }
        }
    }

    fun sendReport(report: PostReportDialog.Report) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                val collectedReports = report.reasons
                val reason = JSONArray(collectedReports).toString()
                model.reportPost(
                    report.contentId.userId,
                    report.contentId.communityId,
                    report.contentId.permlink,
                    reason
                )
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onCleared() {
        loadPostsJob?.cancel()
        super.onCleared()
    }
}