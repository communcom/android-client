package io.golos.cyber_android.ui.screens.post_view.view_model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.services.post_view.RecordPostViewService
import io.golos.cyber_android.ui.dto.DonateType
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.post_view.dto.*
import io.golos.cyber_android.ui.screens.post_view.model.PostPageModel
import io.golos.cyber_android.ui.screens.post_view.model.VoteType
import io.golos.cyber_android.ui.shared.broadcast_actions_registries.PostUpdateRegistry
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.widgets.comment.CommentContent
import io.golos.cyber_android.ui.shared.widgets.comment.ContentState
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.*
import io.golos.domain.posts_parsing_rendering.mappers.editor_output_to_json.EditorOutputToJsonMapper
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.EmbedMetadata
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class PostPageViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: PostPageModel,
    private val moshi: Moshi,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val postContentId: ContentIdDomain,
    private val postUpdateRegistry: PostUpdateRegistry
) : ViewModelBase<PostPageModel>(dispatchersProvider, model),
    PostPageViewModelListEventsProcessor {

    private var wasMovedToChild = false         // We move to child screen from this one

    val commentsPageSize: Int
        get() = model.commentsPageSize

    private val _isPostSubscriptionModified:MutableLiveData<Boolean>  = MutableLiveData(false)
    val isPostSubscriptionModified:LiveData<Boolean>
        get() = _isPostSubscriptionModified

    /**
     * All data in post list (post title, body, controls, commeents etc.)
     */
    val post: LiveData<List<VersionedListItem>> = model.post

    val title:LiveData<String> = model.title

    /**
     * Screen header
     */
    private val _postHeader = MutableLiveData<PostHeader>()
    val postHeader = _postHeader as LiveData<PostHeader>

    private val _commentFieldEnabled = MutableLiveData<Boolean>(false)
    val commentFieldEnabled = _commentFieldEnabled as LiveData<Boolean>

    init {
        RecordPostViewService.record(appContext, postContentId)
        applyPostDonationSendListener()
        applyRewardCurrencyListener()
    }

    fun setup() {
        if (wasMovedToChild) {
            wasMovedToChild = false
            return
        }

        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.loadPost()
                _postHeader.value = model.getPostHeader()

                model.loadStartFirstLevelCommentsPage()

                _commentFieldEnabled.value = true
                _command.value = SetLoadingVisibilityCommand(false)
            } catch (ex: Exception) {
                _command.value = (ex as? ApiResponseErrorException)?.errorInfo?.message
                    ?.let {
                        ShowMessageTextCommand(it)
                    }
                    ?: ShowMessageResCommand(R.string.common_general_error)

                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = NavigateBackwardCommand()
            }
        }
    }

    override fun onImageClicked(imageUri: Uri) {
        wasMovedToChild = true
        _command.value = NavigateToImageViewCommand(imageUri)
    }

    override fun onLinkClicked(linkUri: Uri) {
        wasMovedToChild = true
        _command.value = NavigateToLinkViewCommand(linkUri)
    }

    override fun onSeeMoreClicked(contentId: ContentIdDomain) = false

    override fun onItemClicked(contentId: ContentIdDomain) {}

    override fun onBodyClicked(postContentId: ContentIdDomain?) {
        //Not need use
    }

    override fun onUserClicked(userId: String) {
        launch {
            try {
                val realUserId = model.getUserId(userId)
                if (currentUserRepository.userId != realUserId) {
                    wasMovedToChild = true
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

    override fun onCommunityClicked(communityId: CommunityIdDomain) {
        _command.value = NavigateToCommunityPageCommand(communityId)
    }

    override fun onUpVoteClick() {
        launch {
            try {
                model.upVote(
                    postContentId.communityId,
                    postContentId.userId,
                    postContentId.permlink
                )
            } catch (e: java.lang.Exception) {
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
            }
        }
    }

    override fun onDownVoteClick() {
        launch {
            try {
                model.downVote(
                    postContentId.communityId,
                    postContentId.userId,
                    postContentId.permlink
                )
            } catch (e: Exception) {
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
            }
        }
    }

    override fun onUnVoteClick() {
        launch {
            try {
                model.unVote(
                    postContentId.communityId,
                    postContentId.userId,
                    postContentId.permlink
                )
            } catch (e: Exception) {
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

    override fun onCommentUpVoteClick(commentId: ContentIdDomain) = voteForComment(commentId, VoteType.UP_VOTE)

    override fun onCommentDownVoteClick(commentId: ContentIdDomain) = voteForComment(commentId,  VoteType.DOWN_VOTE)

    override fun onCommentUnVoteClick(commentId: ContentIdDomain) = voteForComment(commentId,  VoteType.UN_VOTE)

    override fun onForbiddenClick() {
        _command.postValue(ShowMessageResCommand(R.string.cant_cancel_vote,true))
    }

    fun onUserInHeaderClick(userId: String) = onUserClicked(userId)

    fun onPostMenuClick() {
        val postMenu: PostMenu = model.getPostMenu()
        _command.value =
            NavigationToPostMenuViewCommand(postMenu)
    }

    fun onPostRewardClick() {
        _command.value = SelectRewardCurrencyDialogCommand(model.rewardCurrency)
    }

    override fun onCommentsTitleMenuClick() {
        _command.value = ShowCommentsSortingMenuViewCommand()
    }

    fun updateRewardCurrency(currency: RewardCurrency) {
        launch {
            model.updateRewardCurrency(currency)
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

    override fun onShareClicked(shareUrl: String) {
        _command.value = SharePostCommand(shareUrl)
    }

    fun editPost(contentId: ContentIdDomain) {
        _command.value =
            NavigationToEditPostViewCommand(contentId)
    }

    fun reportPost(contentId: ContentIdDomain) {
        _command.value = ReportPostCommand(contentId)
    }

    fun deletePost() {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                val permlink = model.deletePost()
                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = NavigationToParentScreenWithStringResultCommand(permlink)
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageTextCommand(ex.getMessage(appContext))
                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = NavigateToMainScreenCommand()
            }
        }
    }

    fun subscribeToCommunity(communityId: CommunityIdDomain) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.subscribeToCommunity(communityId)
                _postHeader.value = _postHeader.value?.copy(
                    isJoinedToCommunity = true
                )
                _isPostSubscriptionModified.value = !_isPostSubscriptionModified.value!!
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
                _postHeader.value = _postHeader.value?.copy(
                    isJoinedToCommunity = false
                )
                _isPostSubscriptionModified.value = !_isPostSubscriptionModified.value!!
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun getCurrentUserId() = currentUserRepository.userId.userId

    override fun onNextCommentsPageReached() = processSimple { model.loadNextFirstLevelCommentsPage() }

    override fun onRetryLoadingFirstLevelCommentButtonClick() = processSimple { model.retryLoadingFirstLevelCommentsPage() }

    override fun onCollapsedCommentsClick(parentCommentId: ContentIdDomain) =
        processSimple {
            model.loadNextSecondLevelCommentsPage(parentCommentId)
        }

    override fun onRetryLoadingSecondLevelCommentButtonClick(parentCommentId: ContentIdDomain) =
        processSimple {
            model.retryLoadingSecondLevelCommentsPage(parentCommentId)
        }

    override fun onCommentLongClick(commentId: ContentIdDomain) {
        val comment = model.getComment(commentId)
        if(comment?.isMyComment == true){
            _command.value =
                ShowCommentMenuViewCommand(commentId)
        }
    }

    fun sendComment(commentContent: CommentContent) {
        launch {
            try {
                _commentFieldEnabled.value = false

                val commentState = commentContent.state

                // Upload images
                val uploadedImages = mutableListOf<String>()
                commentContent.metadata
                    .firstOrNull { it is EmbedMetadata }
                    ?.let { it as EmbedMetadata }
                    ?.let {
                        if(!it.sourceUri.toString().startsWith("http") && !it.sourceUri.toString().startsWith("https")){
                            uploadedImages.add(model.uploadAttachmentContent(File(it.sourceUri.toString())))
                        }
                    }


                val deviceAdapter = moshi.adapter(DeviceInfoEntity::class.java)
                val contentAsJson = EditorOutputToJsonMapper.mapComment(commentContent.metadata, uploadedImages,
                    deviceAdapter.toJson(DeviceInfoEntity(version = BuildConfig.VERSION_NAME)))

                when (commentState) {
                    ContentState.NEW -> {
                        model.sendComment(contentAsJson,commentContent.metadata)
                    }
                    ContentState.EDIT -> {
                        model.updateComment(commentContent.contentId!!, contentAsJson)
                    }
                    ContentState.REPLY -> {
                        model.replyToComment(commentContent.contentId!!, contentAsJson,commentContent.metadata)
                    }
                }


                _command.value = ClearCommentInputCommand()
            } catch (ex: Exception) {
                _command.value = ShowMessageTextCommand(ex.getMessage(appContext))
            } finally {
                _commentFieldEnabled.value = true
            }
        }
    }


    fun deleteComment(commentId: ContentIdDomain) =
        processSimple {
            model.deleteComment(commentId)
        }

    fun startEditComment(commentId: ContentIdDomain) = startReplyOrEditComment {
        val comment = model.getComment(commentId)
        comment?.let {
            val contentBlock = comment.body
            val discussionIdModel = comment.contentId
            val commentContentId = ContentIdDomain(postContentId.communityId, discussionIdModel.permlink, discussionIdModel.userId)
            _command.value = NavigateToEditComment(
                commentContentId,
                contentBlock
            )
        }
    }

    override fun startReplyToComment(commentToReplyId: ContentIdDomain) = startReplyOrEditComment {
        val comment = model.getComment(commentToReplyId)
        comment?.let {
            val contentBlock = comment.body
            val parentContentId = ContentIdDomain(
                postContentId.communityId,
                commentToReplyId.permlink,
                commentToReplyId.userId)
            _command.value = NavigateToReplyCommentViewCommand(
                parentContentId,
                contentBlock
            )
        }
    }

    private fun applyPostDonationSendListener() {
        launch {
            postUpdateRegistry.donationSend.collect {
                it?.let { donationInfo ->
                    model.updateDonation(donationInfo)
                }
            }
        }
    }

    private fun applyRewardCurrencyListener() {
        launch {
            model.rewardCurrencyUpdates.collect {
                it?.let { newRewardCurrency ->
                    _postHeader.value?.reward?.let { oldReward ->
                        val newReward = oldReward.copy(rewardCurrency = newRewardCurrency)
                        _postHeader.value = _postHeader.value!!.copy(reward = newReward)
                    }
                }
            }
        }
    }

    private fun voteForComment(commentId: ContentIdDomain, voteType: VoteType) =
        processSimple { model.voteForComment(postContentId.communityId, commentId, voteType) }

    private fun processSimple(action: suspend () -> Unit) {
        launch {
            try {
                action()
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageTextCommand(ex.getMessage(appContext))
            }
        }
    }

    private fun startReplyOrEditComment(commentAction: () -> Unit) {
        try {
            commentAction()
            //_commentEditFieldVisibility.value = View.VISIBLE
        } catch (ex: Exception) {
            Timber.e(ex)
            _command.value = ShowMessageResCommand(R.string.common_general_error)
            //_commentEditFieldVisibility.value = View.GONE
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

    fun viewInExplorer(postMenu: PostMenu) {
        _command.value = ViewInExplorerViewCommand(postMenu.browseUrl.toString())
    }
    fun backVoteChange() {
        _command.value = ChangeVoteByPostPage()
    }
}