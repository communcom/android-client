package io.golos.cyber_android.ui.shared_fragments.post.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.posts.AbstractFeedWithCommentsViewModel
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.cyber_android.ui.shared_fragments.post.dto.SortingType
import io.golos.cyber_android.ui.shared_fragments.post.model.PostPageModel
import io.golos.cyber_android.ui.shared_fragments.post.view_commands.*
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.CommentEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.feed.PostWithCommentUseCase
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PostPageViewModel
@Inject
constructor(
    postWithCommentUseCase: PostWithCommentUseCase,
    voteUseCase: VoteUseCase,
    posterUseCase: DiscussionPosterUseCase,
    signInUseCase: SignInUseCase,
    private val dispatchersProvider: DispatchersProvider,
    private val model: PostPageModel,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val postToProcess: DiscussionIdModel
) : AbstractFeedWithCommentsViewModel<CommentFeedUpdateRequest, CommentEntity, CommentModel>(
    postWithCommentUseCase as AbstractFeedUseCase<out CommentFeedUpdateRequest, CommentEntity, CommentModel>,
    voteUseCase,
    posterUseCase,
    signInUseCase
), CoroutineScope, PostPageViewModelListEventsProcessor {

    private var wasMovedToChild = false         // We move to child screen from this one

    private val scopeJob: Job = SupervisorJob()

    /**
     * Context of this scope.
     */
    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    val commentsPageSize: Int
        get() = model.commentsPageSize

    /**
     * Direct command for view
     */
    val command: SingleLiveData<ViewCommand> = SingleLiveData()

    /**
     * All data in post list (post title, body, controls, commeents etc.)
     */
    val post: LiveData<List<VersionedListItem>> = model.post

    /**
     * Screen header
     */
    private val _postHeader = MutableLiveData<PostHeader>()
    val postHeader = _postHeader as LiveData<PostHeader>

    fun setup() {
        if(wasMovedToChild) {
            wasMovedToChild = false
            return
        }

        launch {
            try {
                command.value = SetLoadingVisibilityCommand(true)
                model.loadPost()
                _postHeader.value = model.getPostHeader()
                command.value = SetLoadingVisibilityCommand(false)

                model.loadStartFirstLevelCommentsPage()
            } catch (ex: Exception) {
                Timber.e(ex)
                command.value = ShowMessageCommand(R.string.common_general_error)
                command.value = NavigateToMainScreenCommand()
            } finally {
                command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        scopeJob.cancelChildren()
        scopeJob.cancel()
    }

    override fun onImageInPostClick(imageUri: Uri) {
        wasMovedToChild = true
        command.value = NavigateToImageViewCommand(imageUri)
    }

    override fun onLinkInPostClick(link: Uri) {
        wasMovedToChild = true
        command.value = NavigateToLinkViewCommand(link)
    }

    override fun onUserInPostClick(userName: String) {
        launch {
            try {
                command.value = SetLoadingVisibilityCommand(true)

                val userId = model.getUserId(userName)

                wasMovedToChild = true
                command.value = NavigateToUserProfileViewCommand(userId)
            } catch (ex: Exception) {
                Timber.e(ex)
                command.value = ShowMessageCommand(R.string.common_general_error)
            } finally {
                command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onUpVoteClick() = voteForPost(true)

    override fun onDownVoteClick() = voteForPost(false)

    fun onUserInHeaderClick(userId: String) {
        wasMovedToChild = true
        command.value = NavigateToUserProfileViewCommand(userId)
    }

    fun onPostMenuClick() {
        val metadata = model.postMetadata
        command.value = ShowPostMenuViewCommand(
            currentUserRepository.userId == postToProcess.userId,
            metadata.version,
            metadata.type)
    }

    override fun onCommentsTitleMenuClick() {
        command.value = ShowCommentsSortingMenuViewCommand()
    }

    fun editPost() {
        command.value = StartEditPostViewCommand(model.postId)
    }

    fun deletePost() {
        launch {
            try {
                command.value = SetLoadingVisibilityCommand(true)
                model.deletePost()
            } catch (ex: Exception) {
                Timber.e(ex)
                command.value = ShowMessageCommand(R.string.common_general_error)
            } finally {
                command.value = SetLoadingVisibilityCommand(false)
                command.value = NavigateToMainScreenCommand()
            }
        }
    }

    fun updateCommentsSorting(sortingType: SortingType) {
        launch {
            try {
                model.updateCommentsSorting(sortingType)
            } catch (ex: Exception) {
                Timber.e(ex)
                command.value = ShowMessageCommand(R.string.common_general_error)
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

    private fun voteForPost(isUpVote: Boolean) = processSimple { model.voteForPost(isUpVote) }

    private fun processSimple(action: suspend () -> Unit) {
        launch {
            try {
                action()
            } catch (ex: Exception) {
                Timber.e(ex)
                command.value = ShowMessageCommand(R.string.common_general_error)
            }
        }
    }
}