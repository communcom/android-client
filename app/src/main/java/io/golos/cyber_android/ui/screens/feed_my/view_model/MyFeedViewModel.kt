package io.golos.cyber_android.ui.screens.feed_my.view_model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.DonateType
import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.RewardCurrency
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.mappers.*
import io.golos.cyber_android.ui.screens.feed_my.dto.SwitchToProfileTab
import io.golos.cyber_android.ui.screens.feed_my.model.MyFeedModel
import io.golos.cyber_android.ui.screens.feed_my.view.view_commands.*
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.shared.broadcast_actions_registries.PostUpdateRegistry
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
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject

class MyFeedViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: MyFeedModel,
    private val paginator: Paginator.Store<Post>,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val postUpdateRegistry: PostUpdateRegistry,
    val recordPostViewManager: RecordPostViewManager
) : ViewModelBase<MyFeedModel>(dispatchersProvider, model),
    MyFeedListListener {

    private val _postsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    val postsListState = _postsListState.toLiveData()

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user = _user.toLiveData()

    private lateinit var postsConfigurationDomain: PostsConfigurationDomain

    private val _loadUserProgressVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val loadUserProgressVisibility = _loadUserProgressVisibility.toLiveData()

    private val _loadUserErrorVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val loadUserErrorVisibility = _loadUserErrorVisibility.toLiveData()

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing get() = _swipeRefreshing.toLiveData()

    private val _rewardCurrency = MutableLiveData<RewardCurrency>(model.rewardCurrency)
    val rewardCurrency: LiveData<RewardCurrency> =_rewardCurrency

    private var loadPostsJob: Job? = null

    init {
        applyFiltersListener()
        applyChangeTabFilterListener()

        paginator.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadMorePosts(it.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {

                }
            }
        }
        paginator.render = { newState, _ ->
            _postsListState.value = newState
        }

        applyAvatarChangeListener()
        applyPostCreatedChangeListener()
        applyPostUpdatedChangeListener()
        applyPostDonationSendListener()
        applyRewardCurrencyListener()
    }

    override fun onShareClicked(shareUrl: String) {
        _command.value = SharePostCommand(shareUrl)
    }

    override fun onBodyClicked(postContentId: ContentIdDomain?) {
        openPost(postContentId)
    }

    fun onPostClicked(postContentId: ContentIdDomain?) {
        openPost(postContentId)
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

    override fun onDonateClick(
        donate: DonateType,
        contentId: ContentIdDomain,
        communityId: CommunityIdDomain,
        contentAuthor: UserBriefDomain) {
        launch {
            _command.value = NavigateToDonateCommand.build(donate, contentId, communityId, contentAuthor, model.getWalletBalance())
        }
    }

    override fun onDonatePopupClick(donates: DonationsDomain) {
        _command.value = ShowDonationUsersDialogCommand(donates)
    }

    override fun onLinkClicked(linkUri: Uri) {
        _command.value = NavigateToLinkViewCommand(linkUri)
    }

    override fun onImageClicked(imageUri: Uri) {
        _command.value = NavigateToImageViewCommand(imageUri)
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

    override fun onUserClicked(userId: String) {
        launch {
            try {
                val realUserId = model.getUserId(userId)
                if (currentUserRepository.userId != realUserId) {
                    _command.value = NavigateToUserProfileCommand(realUserId)
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = if(ex is ApiResponseErrorException && ex.message != null) {
                    ShowMessageTextCommand(ex.message!!)
                } else {
                    ShowMessageResCommand(R.string.common_general_error)
                }
            }
        }
    }

    fun onCurrentUserClicked() {
        _command.value = SwitchToProfileTab()
    }

    override fun onCommunityClicked(communityId: CommunityIdDomain) {
        _command.value = NavigateToCommunityPageCommand(communityId)
    }

    override fun onMenuClicked(postMenu: PostMenu) {
        _command.value = NavigationToPostMenuViewCommand(postMenu)
    }

    override fun onRewardClick(reward: RewardPostDomain?) {
        _command.value = SelectRewardCurrencyDialogCommand(model.rewardCurrency)
    }

    override fun onCommentsClicked(postContentId: ContentIdDomain) {
        openPost(postContentId)
    }

    fun updateRewardCurrency(currency: RewardCurrency) {
        launch {
            model.updateRewardCurrency(currency)
        }
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
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun onReportPostClicked(permlink: String) {
        val post = getPostFromPostsListState(permlink)
        post?.let {
            _command.value = ReportPostCommand(post)
        }
    }

    fun onSwipeRefresh() {
        launch {
            paginator.initState(Paginator.State.Empty)
            restartLoadPosts()

            _swipeRefreshing.value = false
        }
    }

    private fun applyAvatarChangeListener(){
        launch {
            try {
                model.userAvatarFlow.collect{
                    val userUpdated = _user.value?.copy(avatarUrl = it)
                    userUpdated?.let {_user.value = it}
                }
            } catch (e: Exception){
                Timber.e(e)
            }
        }
    }

    private fun applyPostCreatedChangeListener() {
        launch {
            postUpdateRegistry.createdPosts.collect {
                it?.let {
                    paginator.initState(Paginator.State.Empty)
                    restartLoadPosts()
                }
            }
        }
    }

    private fun applyPostUpdatedChangeListener() {
        launch {
            postUpdateRegistry.updatedPosts.collect {
                it?.let {
                    paginator.proceed(Paginator.Action.UpdateItem(it.mapToPost()))
                }
            }
        }
    }

    private fun applyPostDonationSendListener() {
        launch {
            postUpdateRegistry.donationSend.collect {
                it?.let { donationInfo ->
                    paginator.proceed(Paginator.Action.UpdateItemById<Post>(donationInfo.postId) { post ->
                        post.copy(donation = donationInfo.donation )
                    })
                }
            }
        }
    }

    private fun applyRewardCurrencyListener() {
        launch {
            model.rewardCurrencyUpdates.collect {
                it?.let { newRewardCurrency -> _rewardCurrency.value = newRewardCurrency }
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

    private fun updateUpVoteInPostsByContentId(posts: ArrayList<Post>, contentId: ContentIdDomain) {
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

    private fun applyFiltersListener() {
        launch {
            model.feedFiltersFlow.collect {
                if (::postsConfigurationDomain.isInitialized) {
                    val feedType = it.updateTimeFilter.mapToTypeFeedDomain()
                    val feedTimeFrame = it.periodTimeFilter.mapToTimeFrameDomain()
                    if (feedType != postsConfigurationDomain.typeFeed ||
                        feedTimeFrame != postsConfigurationDomain.timeFrame
                    ) {
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

    private fun applyChangeTabFilterListener() {
        launch {
            model.openFeedTypeFlow.collect { filter ->
                Timber.d("FILTER: -> ${filter.name}")
                paginator.initState(Paginator.State.Empty)
                restartLoadPosts()
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

    fun loadMorePosts() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    private fun loadMorePosts(pageCount: Int) {
        Timber.d("paginator: load posts on page -> $pageCount")
        loadPostsJob = launch {
            try {
                postsConfigurationDomain = postsConfigurationDomain.copy(offset = pageCount * PAGINATION_PAGE_SIZE)
                val openTypeFeed = model.openFeedTypeFlow.first()
                val typeObjectDomain = if (openTypeFeed == PostFiltersHolder.CurrentOpenTypeFeed.MY_FEED) {
                    TypeObjectDomain.MY_FEED
                } else {
                    TypeObjectDomain.TRENDING
                }
                val postsDomainList = model.getPosts(postsConfigurationDomain, typeObjectDomain)
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
                val feedFilters = model.feedFiltersFlow.first()
                val feedType = feedFilters.updateTimeFilter.mapToTypeFeedDomain()
                val feedTimeFrame = feedFilters.periodTimeFilter.mapToTimeFrameDomain()
                postsConfigurationDomain = PostsConfigurationDomain(
                    userProfile.id,
                    null,
                    PostsConfigurationDomain.SortByDomain.TIME_DESC,
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

    fun onCreatePostClicked() {
        _command.value = CreatePostCommand()
    }

    fun viewInExplorer(postMenu: PostMenu) {
        _command.value = ViewInExplorerViewCommand(postMenu.browseUrl.toString())
    }
}