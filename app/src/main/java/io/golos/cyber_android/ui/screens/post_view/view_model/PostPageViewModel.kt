package io.golos.cyber_android.ui.screens.post_view.view_model

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.post_view.dto.EditReplyCommentSettings
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.screens.post_view.model.PostPageModel
import io.golos.cyber_android.ui.screens.post_view.view_commands.*
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject

class PostPageViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostPageModel,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val postToProcess: DiscussionIdModel,
    private val contentId: ContentId?
) : ViewModelBase<PostPageModel>(dispatchersProvider, model),
    PostPageViewModelListEventsProcessor {

    private var wasMovedToChild = false         // We move to child screen from this one

    private var editedCommentId: DiscussionIdModel? = null
    private var repliedCommentId: DiscussionIdModel? = null

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

    private val _commentEditFieldEnabled = MutableLiveData<Boolean>(true)
    val commentEditFieldEnabled = _commentFieldEnabled as LiveData<Boolean>

    private val _commentFieldVisibility = MutableLiveData<Int>(View.VISIBLE)
    val commentFieldVisibility = _commentFieldVisibility as LiveData<Int>

    private val _commentEditFieldVisibility = MutableLiveData<Int>(View.GONE)
    val commentEditFieldVisibility = _commentEditFieldVisibility as LiveData<Int>

    private val _commentEditFieldSettings =
        MutableLiveData<EditReplyCommentSettings>(EditReplyCommentSettings(listOf(), listOf(), true))
    val commentEditFieldSettings = _commentEditFieldSettings as LiveData<EditReplyCommentSettings>

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

    override fun onUserClicked(userId: String) {
        if (currentUserRepository.userId.userId == userId) {
            return
        }

        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)

                val userId = model.getUserId(userId)

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
                    contentId?.communityId.orEmpty(),
                    contentId?.userId.orEmpty(),
                    contentId?.permlink.orEmpty()
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
                    contentId?.communityId.orEmpty(),
                    contentId?.userId.orEmpty(),
                    contentId?.permlink.orEmpty()
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
        _command.value = NavigationToPostMenuViewCommand(postMenu)
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
        val body = model.getCommentBody(contentId)
        _command.value = NavigateToEditComment(contentId, body)
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
        _command.value = ShowCommentMenuViewCommand(commentId)
    }

    fun onSendCommentClick(commentText: String) {
        launch {
            try {
                _commentFieldEnabled.value = false
                model.sendComment(commentText)
                _command.value = ClearCommentTextViewCommand()
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
        _commentEditFieldSettings.value = model.getCommentText(commentId).let { EditReplyCommentSettings(it, it, true) }
        editedCommentId = commentId
    }

    override fun startReplyToComment(commentToReplyId: DiscussionIdModel) = startReplyOrEditComment {
        _commentEditFieldSettings.value = EditReplyCommentSettings(model.getCommentText(commentToReplyId), listOf(), false)
        repliedCommentId = commentToReplyId
    }

    fun cancelReplyOrEditComment() {
        _commentEditFieldSettings.value = EditReplyCommentSettings(listOf(), listOf(), true)

        _commentFieldVisibility.value = View.VISIBLE
        _commentEditFieldVisibility.value = View.GONE

        editedCommentId = null
        repliedCommentId = null
    }

    fun completeReplyOrEditComment(newCommentText: String) =
        completeReplyOrEditComment {
            when {
                editedCommentId != null -> model.updateCommentText(editedCommentId!!, newCommentText)
                repliedCommentId != null -> model.replyToComment(repliedCommentId!!, newCommentText)
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

            _commentFieldVisibility.value = View.GONE
            _commentEditFieldVisibility.value = View.VISIBLE
        } catch (ex: Exception) {
            Timber.e(ex)
            _command.value = ShowMessageResCommand(R.string.common_general_error)

            _commentFieldVisibility.value = View.VISIBLE
            _commentEditFieldVisibility.value = View.GONE
        }
    }

    private fun completeReplyOrEditComment(commentAction: suspend () -> Unit) {
        launch {
            try {
                _commentEditFieldEnabled.value = false
                commentAction()
                cancelReplyOrEditComment()
            } catch (ex: Exception) {
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            } finally {
                _commentEditFieldEnabled.value = true
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
}