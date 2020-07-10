package io.golos.cyber_android.ui.screens.community_page_post.view_model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.dto.DonateType
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.mappers.mapToPostsList
import io.golos.cyber_android.ui.mappers.mapToTimeFrameDomain
import io.golos.cyber_android.ui.mappers.mapToTypeFeedDomain
import io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.model.TimeConfigurationDomain
import io.golos.cyber_android.ui.screens.community_page_post.model.CommunityPostModel
import io.golos.cyber_android.ui.screens.community_page_post.view.*
import io.golos.cyber_android.ui.screens.feed_my.view_model.MyFeedListListener
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
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject

class CommunityPostViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: CommunityPostModel,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val communityId: CommunityIdDomain,
    private val paginator: Paginator.Store<Post>,
    val recordPostViewManager: RecordPostViewManager,
    private val postUpdateRegistry: PostUpdateRegistry
) : ViewModelBase<CommunityPostModel>(dispatchersProvider, model), MyFeedListListener {

    private val _postsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)
    val postsListState = _postsListState.toLiveData()

    private val _filterUpdateTime: MutableLiveData<TimeConfigurationDomain> =
        MutableLiveData(
            TimeConfigurationDomain(
                PostFiltersHolder.UpdateTimeFilter.NEW,
                PostFiltersHolder.PeriodTimeFilter.ALL
            )
        )
    val filterPostState = _filterUpdateTime.toLiveData()

    private var loadPostsJob: Job? = null
    private lateinit var postsConfigurationDomain: PostsConfigurationDomain

    private val _rewardCurrency = MutableLiveData<RewardCurrency>(model.rewardCurrency)
    val rewardCurrency: LiveData<RewardCurrency> =_rewardCurrency

    init {
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

        updateFilterAndLoadPosts()
        applyPostDonationSendListener()
        applyRewardCurrencyListener()
    }

    fun loadFilter() {
        val configuration = _filterUpdateTime.value
        configuration?.let { config ->
            _command.value = NavigateToFilterDialogViewCommand(config)
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

    private fun updateFilterAndLoadPosts() {
        val timeConfiguration = _filterUpdateTime.value
        timeConfiguration?.let { config ->
            postsConfigurationDomain = PostsConfigurationDomain(
                currentUserRepository.userId.userId,
                communityId,
                PostsConfigurationDomain.SortByDomain.TIME_DESC,
                config.periodFilter.mapToTimeFrameDomain(),
                PAGINATION_PAGE_SIZE,
                0,
                config.timeFilter.mapToTypeFeedDomain()
            )
            restartLoadPosts()
        }
    }

    fun updatePostsByFilter(
        filterTime: PostFiltersHolder.UpdateTimeFilter,
        periodTime: PostFiltersHolder.PeriodTimeFilter
    ) {
        _filterUpdateTime.value = TimeConfigurationDomain(filterTime, periodTime)
        updateFilterAndLoadPosts()
    }

    override fun onMenuClicked(postMenu: PostMenu) {
        _command.value = NavigationToPostMenuViewCommand(postMenu)
    }

    override fun onRewardClick(reward: RewardPostDomain?) {
        _command.value = SelectRewardCurrencyDialogCommand(model.rewardCurrency)
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
        if (currentUserRepository.userId.userId != userId) {
            _command.value = NavigateToUserProfileCommand(UserIdDomain(userId))
        }
    }

    override fun onCommunityClicked(communityId: CommunityIdDomain) {}

    override fun onCommentsClicked(postContentId: ContentIdDomain) {
        openPost(postContentId)
    }

    override fun onBodyClicked(postContentId: ContentIdDomain?) {
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

    override fun onDonateClick(donate: DonateType, contentId: ContentIdDomain, communityId: CommunityIdDomain, contentAuthor: UserBriefDomain) {
        launch {
            _command.value = NavigateToDonateCommand.build(donate, contentId, communityId, contentAuthor, model.getWalletBalance())
        }
    }

    override fun onDonatePopupClick(donates: DonationsDomain) {
        _command.value = ShowDonationUsersDialogCommand(donates)
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
        post?.let { _command.value = ReportPostCommand(post) }
    }

    fun sendReport(report: PostReportDialog.Report) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                val collectedReports = report.reasons
                val reason = JSONArray(collectedReports).toString()
                model.reportPost(
                    authorPostId = report.contentId.userId,
                    communityId = report.contentId.communityId,
                    permlink = report.contentId.permlink,
                    reason = reason
                )
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    private fun loadMorePosts(pageCount: Int) {
        loadPostsJob = launch {
            try {
                postsConfigurationDomain = postsConfigurationDomain.copy(offset = pageCount * PAGINATION_PAGE_SIZE)
                val postsDomainList = model.getPosts(postsConfigurationDomain, TypeObjectDomain.COMMUNITY)
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

    private fun getPostFromPostsListState(permlink: String): Post? {
        when (postsListState.value) {
            is Paginator.State.Data<*> -> {
                return ((postsListState.value as Paginator.State.Data<*>).data as List<Post>)
                    .find { post ->
                        post.contentId.permlink == permlink
                    }
            }
            is Paginator.State.Refresh<*> -> {
                return ((postsListState.value as Paginator.State.Refresh<*>).data as List<Post>)
                    .find { post ->
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
                return ((postsListState.value as Paginator.State.FullData<*>).data as List<Post>)
                    .find { post ->
                        post.contentId.permlink == permlink
                    }
            }
        }
        return null
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
        val foundedPost = posts.find { post -> post.contentId.permlink == permlink }?.copy()
        foundedPost?.let { post -> posts.remove(post) }
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

    override fun onCleared() {
        loadPostsJob?.cancel()
        super.onCleared()
    }
}