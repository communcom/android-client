package io.golos.cyber_android.ui.screens.post_view.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.post_view.dto.*
import io.golos.cyber_android.ui.screens.post_view.model.PostPageModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.widgets.comment.CommentContent
import io.golos.cyber_android.ui.shared.widgets.comment.ContentState
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.posts_parsing_rendering.mappers.editor_output_to_json.EditorOutputToJsonMapper
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.EmbedMetadata
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class PostPageViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostPageModel,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val postContentId: ContentId
) : ViewModelBase<PostPageModel>(dispatchersProvider, model),
    PostPageViewModelListEventsProcessor {

    private var wasMovedToChild = false         // We move to child screen from this one

    val commentsPageSize: Int
        get() = model.commentsPageSize

    /**
     * All data in post list (post title, body, controls, commeents etc.)
     */
    val post: LiveData<List<VersionedListItem>> = model.post

    /**
     * Screen header
     */
    private val _postHeader = MutableLiveData<PostHeader>()
    val postHeader = _postHeader as LiveData<PostHeader>

    private val _commentFieldEnabled = MutableLiveData<Boolean>(false)
    val commentFieldEnabled = _commentFieldEnabled as LiveData<Boolean>

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

    override fun onSeeMoreClicked(contentId: ContentId) = false

    override fun onItemClicked(contentId: ContentId) {}

    override fun onBodyClicked(postContentId: ContentId?) {
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
                _command.value = ShowMessageResCommand(R.string.common_general_error)
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
                _command.value = ShowMessageResCommand(R.string.common_general_error)
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
            } catch (e: java.lang.Exception) {
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }

    override fun onCommentUpVoteClick(commentId: DiscussionIdModel) = voteForComment(commentId, true)

    override fun onCommentDownVoteClick(commentId: DiscussionIdModel) = voteForComment(commentId, false)

    fun onUserInHeaderClick(userId: String) = onUserClicked(userId)

    fun onPostMenuClick() {
        val postMenu: PostMenu = model.getPostMenu()
        _command.value =
            NavigationToPostMenuViewCommand(postMenu)
    }

    fun onPostRewardClick() {
        model.isTopReward()?.let {
            val title = if(it) R.string.post_reward_top_title else R.string.post_reward_not_top_title
            val text = if(it) R.string.post_reward_top_text else R.string.post_reward_not_top_text
            _command.value = ShowPostRewardDialogCommand(title, text)
        }
    }

    override fun onCommentsTitleMenuClick() {
        _command.value = ShowCommentsSortingMenuViewCommand()
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

    fun editPost(contentId: ContentId) {
        _command.value =
            NavigationToEditPostViewCommand(contentId)
    }

    fun reportPost(contentId: ContentId) {
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
                _command.value = ShowMessageResCommand(R.string.common_general_error)
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
            } catch (e: java.lang.Exception) {
                Timber.e(e)
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
            } catch (e: java.lang.Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onNextCommentsPageReached() = processSimple { model.loadNextFirstLevelCommentsPage() }

    override fun onRetryLoadingFirstLevelCommentButtonClick() = processSimple { model.retryLoadingFirstLevelCommentsPage() }

    override fun onCollapsedCommentsClick(parentCommentId: DiscussionIdModel) =
        processSimple {
            model.loadNextSecondLevelCommentsPage(parentCommentId)
        }

    override fun onRetryLoadingSecondLevelCommentButtonClick(parentCommentId: DiscussionIdModel) =
        processSimple {
            model.retryLoadingSecondLevelCommentsPage(parentCommentId)
        }

    override fun onCommentLongClick(commentId: DiscussionIdModel) {
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

                val contentAsJson = EditorOutputToJsonMapper.mapComment(commentContent.metadata, uploadedImages)

                when (commentState) {
                    ContentState.NEW -> {
                        model.sendComment(contentAsJson)
                    }
                    ContentState.EDIT -> {
                        val currentMessageId = commentContent.contentId!!
                        model.updateComment(DiscussionIdModel(currentMessageId.userId, Permlink(currentMessageId.permlink)), contentAsJson)
                    }
                    ContentState.REPLY -> {
                        val repliedMessageId = commentContent.contentId!!
                        model.replyToComment(DiscussionIdModel(repliedMessageId.userId, Permlink(repliedMessageId.permlink)), contentAsJson)
                    }
                }


                _command.value = ClearCommentInputCommand()
            } catch (ex: Exception) {
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            } finally {
                _commentFieldEnabled.value = true
            }
        }
    }

    fun deleteComment(commentId: DiscussionIdModel) =
        processSimple {
            model.deleteComment(commentId)
        }

    fun startEditComment(commentId: DiscussionIdModel) = startReplyOrEditComment {
        val comment = model.getComment(commentId)
        comment?.let {
            val contentBlock = comment.body
            val discussionIdModel = comment.contentId
            val commentContentId = ContentId(postContentId.communityId, discussionIdModel.permlink.value, discussionIdModel.userId)
            _command.value = NavigateToEditComment(
                commentContentId,
                contentBlock
            )
        }
    }

    override fun startReplyToComment(commentToReplyId: DiscussionIdModel) = startReplyOrEditComment {
        val comment = model.getComment(commentToReplyId)
        comment?.let {
            val contentBlock = comment.body
            val parentContentId = ContentId(postContentId.communityId, commentToReplyId.permlink.value, commentToReplyId.userId)
            _command.value = NavigateToReplyCommentViewCommand(
                parentContentId,
                contentBlock
            )
        }
    }

    private fun voteForComment(commentId: DiscussionIdModel, isUpVote: Boolean) =
        processSimple { model.voteForComment(postContentId.communityId, commentId, isUpVote) }

    private fun processSimple(action: suspend () -> Unit) {
        launch {
            try {
                action()
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
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
}