package io.golos.cyber_android.ui.shared_fragments.post.view_model

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared_fragments.post.dto.EditReplyCommentSettings
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.cyber_android.ui.shared_fragments.post.dto.SortingType
import io.golos.cyber_android.ui.shared_fragments.post.model.PostPageModel
import io.golos.cyber_android.ui.shared_fragments.post.view_commands.*
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.DispatchersProvider
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class PostPageViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostPageModel,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val postToProcess: DiscussionIdModel
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

    private val _commentEditFieldSettings = MutableLiveData<EditReplyCommentSettings>(EditReplyCommentSettings(listOf(), listOf(), true))
    val commentEditFieldSettings = _commentEditFieldSettings as LiveData<EditReplyCommentSettings>

    fun setup() {
        if(wasMovedToChild) {
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
                _command.value = ShowMessageCommand(R.string.common_general_error)
                _command.value = NavigateToMainScreenCommand()
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
                _commentFieldEnabled.value = true
            }
        }
    }

    override fun onImageInPostClick(imageUri: Uri) {
        wasMovedToChild = true
        _command.value = NavigateToImageViewCommand(imageUri)
    }

    override fun onLinkInPostClick(link: Uri) {
        wasMovedToChild = true
        _command.value = NavigateToLinkViewCommand(link)
    }

    override fun onUserInPostClick(userName: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)

                val userId = model.getUserId(userName)

                wasMovedToChild = true
                _command.value = NavigateToUserProfileViewCommand(userId)
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageCommand(R.string.common_general_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onUpVoteClick() = voteForPost(true)

    override fun onDownVoteClick() = voteForPost(false)

    override fun onCommentUpVoteClick(commentId: DiscussionIdModel) = voteForComment(commentId, true)

    override fun onCommentDownVoteClick(commentId: DiscussionIdModel) = voteForComment(commentId, false)

    fun onUserInHeaderClick(userId: String) {
        wasMovedToChild = true
        _command.value = NavigateToUserProfileViewCommand(userId)
    }

    fun onPostMenuClick() {
        val metadata = model.postMetadata
        _command.value = ShowPostMenuViewCommand(
            currentUserRepository.userId == postToProcess.userId,
            metadata.version,
            metadata.type)
    }

    override fun onCommentsTitleMenuClick() {
        _command.value = ShowCommentsSortingMenuViewCommand()
    }

    fun editPost() {
        _command.value = StartEditPostViewCommand(model.postId)
    }

    fun deletePost() {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.deletePost()
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageCommand(R.string.common_general_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
                _command.value = NavigateToMainScreenCommand()
            }
        }
    }

    fun updateCommentsSorting(sortingType: SortingType) {
        launch {
            try {
                model.updateCommentsSorting(sortingType)
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageCommand(R.string.common_general_error)
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
            } catch(ex: Exception) {
                _command.value = ShowMessageCommand(R.string.common_general_error)
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

    override fun startReplyToComment(commentToReplyId: DiscussionIdModel)  = startReplyOrEditComment {
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

    private fun voteForPost(isUpVote: Boolean) = processSimple { model.voteForPost(isUpVote) }

    private fun voteForComment(commentId: DiscussionIdModel, isUpVote: Boolean) =
        processSimple { model.voteForComment(commentId, isUpVote) }

    private fun processSimple(action: suspend () -> Unit) {
        launch {
            try {
                action()
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageCommand(R.string.common_general_error)
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
            _command.value = ShowMessageCommand(R.string.common_general_error)

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
            } catch(ex: Exception) {
                _command.value = ShowMessageCommand(R.string.common_general_error)
            } finally {
                _commentEditFieldEnabled.value = true
            }
        }
    }
}