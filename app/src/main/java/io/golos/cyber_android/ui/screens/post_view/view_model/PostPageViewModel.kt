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
import io.golos.cyber_android.ui.shared.utils.localSize
import io.golos.cyber_android.ui.shared.widgets.CommentWidget
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.post.post_dto.AttachmentsBlock
import io.golos.domain.use_cases.post.post_dto.ImageBlock
import io.golos.domain.use_cases.post.post_dto.ParagraphBlock
import io.golos.domain.use_cases.post.post_dto.TextBlock
import io.golos.domain.utils.IdUtil
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
                _command.value = SetLoadingVisibilityCommand(false)

                model.loadStartFirstLevelCommentsPage()
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
                _command.value = NavigateToMainScreenCommand()
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
                _commentFieldEnabled.value = true
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

    override fun onSeeMoreClicked(contentId: ContentId) {}

    override fun onItemClicked(contentId: ContentId) {}

    override fun onBodyClicked(postContentId: ContentId?) {
        //Not need use
    }

    override fun onUserClicked(userId: String) {
        if (currentUserRepository.userId.userId == userId) {
            return
        }

        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                wasMovedToChild = true
                _command.value = NavigateToUserProfileCommand(UserIdDomain(userId))
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onCommunityClicked(communityId: String) {
        _command.value = NavigateToCommunityPageCommand(communityId)
    }

    override fun onUpVoteClick() {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.upVote(
                    postContentId?.communityId.orEmpty(),
                    postContentId?.userId.orEmpty(),
                    postContentId?.permlink.orEmpty()
                )
            } catch (e: java.lang.Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onDownVoteClick() {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.downVote(
                    postContentId?.communityId.orEmpty(),
                    postContentId?.userId.orEmpty(),
                    postContentId?.permlink.orEmpty()
                )
            } catch (e: java.lang.Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onCommentUpVoteClick(commentId: DiscussionIdModel) = voteForComment(commentId, true)

    override fun onCommentDownVoteClick(commentId: DiscussionIdModel) = voteForComment(commentId, false)

    fun onUserInHeaderClick(userId: String) {
        if (currentUserRepository.userId.userId != userId) {
            wasMovedToChild = true
            _command.value = NavigateToUserProfileCommand(UserIdDomain(userId))
        }
    }

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

    fun onShareClicked(shareUrl: String) {
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

    fun subscribeToCommunity(communityId: String) {
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

    fun unsubscribeToCommunity(communityId: String) {
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

    fun sendComment(commentContent: CommentWidget.CommentContent) {
        launch {
            try {
                _commentFieldEnabled.value = false

                val commentState = commentContent.state

                val content = commentContent.message?.let { message ->
                    listOf(ParagraphBlock(null, listOf(TextBlock(IdUtil.generateLongId(), message, null, null))))
                } ?: listOf()
                var imageUri = commentContent.imageUri

                if(imageUri != null){
                    if(!imageUri.toString().startsWith("http") && !imageUri.toString().startsWith("https")){
                        //файл выбран локально и должен быть загружен
                        imageUri = Uri.parse(model.uploadAttachmentContent(File(imageUri.toString())))
                    }
                }
                val attachments = imageUri?.let { uri ->
                    val localSize = commentContent.imageUri?.localSize()
                    val widthImage = if (localSize?.x == 0) null else localSize?.x
                    val heightImage = if (localSize?.y == 0) null else localSize?.y
                    AttachmentsBlock(IdUtil.generateLongId(),
                        listOf(
                            ImageBlock(null,
                                uri,
                                null,
                                widthImage,
                                heightImage)
                        ))
                }

                when (commentState) {
                    CommentWidget.ContentState.NEW -> {
                        model.sendComment(content, attachments)
                    }
                    CommentWidget.ContentState.EDIT -> {
                        val currentMessageId = commentContent.contentId!!
                        model.updateComment(DiscussionIdModel(currentMessageId.userId, Permlink(currentMessageId.permlink)), content, attachments)
                    }
                    CommentWidget.ContentState.REPLY -> {
                        val repliedMessageId = commentContent.contentId!!
                        model.replyToComment(DiscussionIdModel(repliedMessageId.userId, Permlink(repliedMessageId.permlink)), content, attachments)
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
        processSimple { model.voteForComment(commentId, isUpVote) }

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