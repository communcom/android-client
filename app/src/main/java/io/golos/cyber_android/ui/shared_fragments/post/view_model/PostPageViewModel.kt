package io.golos.cyber_android.ui.shared_fragments.post.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import io.golos.cyber_android.ui.shared_fragments.post.model.PostPageModel
import io.golos.cyber_android.ui.shared_fragments.post.view_commands.*
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.CommentEntity
import io.golos.domain.extensions.distinctUntilChanged
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.feed.PostWithCommentUseCase
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.VoteRequestModel
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
), CoroutineScope, PostPageViewModelItemsClickProcessor {

    private var wasMovedToChild = false         // We move to child screen from this one

    enum class Visibility {
        VISIBLE, GONE
    }

    private val scopeJob: Job = SupervisorJob()

    /**
     * Context of this scope.
     */
    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    /**
     * Direct command for view
     */
    val command: SingleLiveData<ViewCommand> = SingleLiveData()

//    private val isActiveUserPostLiveData =
//        postWithCommentUseCase.getPostAsLiveData.combinedWith(signInUseCase.getAsLiveData) { post, authState ->
//            post != null && authState != null &&
//                    (post.contentId.userId.toCyberName() == authState.userName)
//        }

    private val _post = MutableLiveData<List<VersionedListItem>>()
    val post: LiveData<List<VersionedListItem>> = _post
    //val postLiveData //= postWithCommentUseCase.getPostAsLiveData

    private val _postHeader = MutableLiveData<PostHeader>()
    val postHeader: LiveData<PostHeader> = _postHeader

    private val discussionToReplyLiveData = MutableLiveData<DiscussionIdModel?>(null)

    /**
     * [LiveData] currently selected discussion to reply
     */
    val getDiscussionToReplyLiveData = discussionToReplyLiveData as LiveData<DiscussionIdModel?>


    private val commentValidnessLiveData = MutableLiveData(false)

    /**
     * [LiveData] that indicates validness of the user comment
     */
    val getCommentValidnessLiveData = commentValidnessLiveData as LiveData<Boolean>


    private val commentInputVisibilityLiveData = MutableLiveData(Visibility.GONE)

    /**
     * [LiveData] that indicates visibility of the comment input widget
     */
    val getCommentInputVisibilityLiveData = commentInputVisibilityLiveData as LiveData<Visibility>


    private val fullyLoadedLiveData = MediatorLiveData<Boolean>().apply {
        var feedReady = false
        var postReady = false
        addSource(feedLiveData) {
            feedReady = true
            postLoadingStatus(feedReady, postReady)
        }

//        addSource(postLiveData) {
//            postReady = true
//            postLoadingStatus(feedReady, postReady)
//        }

        addSource(loadingStatusLiveData) {
            postLoadingStatus(feedReady, postReady)
        }
    }

    fun setup() {
        if(wasMovedToChild) {
            wasMovedToChild = false
            return
        }

        launch {
            try {
                command.value = SetLoadingVisibilityCommand(true)

                _post.value = model.getPost()
                _postHeader.value = model.getPostHeader()
            } catch (ex: Exception) {
                Timber.e(ex)
                command.value = ShowMessageCommand(R.string.common_general_error)
                command.value = NavigateToMainScreenCommand()
            } finally {
                command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    /**
     * [LiveData] that post is fully loaded (including post itself and first page of comments)
     */
    val getFullyLoadedLiveData = fullyLoadedLiveData.distinctUntilChanged() as LiveData<Boolean>

    /**
     * Sets [DiscussionIdModel] which should be used as a parent of a new comment (created via [sendComment])
     */
    fun setDiscussionToReply(id: DiscussionIdModel) {
        discussionToReplyLiveData.postValue(id)
    }

    /**
     * Clears currently selected [DiscussionIdModel] to reply
     */
    fun clearDiscussionToReply() {
        discussionToReplyLiveData.postValue(null)
    }

    override fun onCleared() {
        super.onCleared()

        scopeJob.cancelChildren()
        scopeJob.cancel()
    }

    /**
     * Sends comment to post associated with this view model. Method checks if there is selected [DiscussionIdModel] to reply
     * and send it there or send it as a top level comment
     */
    fun sendComment(text: CharSequence) {
//        if (discussionToReplyLiveData.value == null) {
//            postLiveData.value?.let {
//                sendComment(it, text)
//            }
//        } else {
//            discussionToReplyLiveData.value?.let {
//                sendComment(it, text)
//            }
//        }
    }

    fun onPostDownvote() {
//        postLiveData.value?.let {
//            val power = if (!it.votes.hasDownVote) (-10_000).toShort() else 0.toShort()
//            voteForPost(power, it)
//        }
    }

    fun onPostUpvote() {
//        postLiveData.value?.let {
//            val power = if (!it.votes.hasUpVote) 10_000.toShort() else 0.toShort()
//            voteForPost(power, it)
//        }
    }

    override fun vote(power: Short, discussionModel: CommentModel) {
        if (!discussionModel.votes.hasUpVoteProgress
            && !discussionModel.votes.hasDownVotingProgress
            && !discussionModel.votes.hasVoteCancelProgress
        ) {
            val request = VoteRequestModel.VoteForComentRequest(power, discussionModel.contentId)
            vote(request, discussionModel.contentId)
        }
    }


    private fun voteForPost(power: Short, discussionModel: PostModel) {
        if (!discussionModel.votes.hasUpVoteProgress
            && !discussionModel.votes.hasDownVotingProgress
            && !discussionModel.votes.hasVoteCancelProgress
        ) {
            val request = VoteRequestModel.VoteForPostRequest(power, discussionModel.contentId)
            vote(request, discussionModel.contentId)
        }
    }

    private var currentCommentText: CharSequence = ""

    fun onCommentChanged(text: CharSequence) {
        currentCommentText = text
        commentValidnessLiveData.postValue(validateComment(text))
    }

    override fun validateComment(comment: CharSequence): Boolean {
        return (super.validateComment(comment)
                && (discussionToReplyLiveData.value == null
                || comment.length > discussionToReplyLiveData.value!!.userId.length + 1))
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

    override fun onUpVoteClick() {
        // See onPostUpvote
    }

    override fun onDownVoteClick() {
        // See onPostUpvote
    }

    fun onUserInHeaderClick(userId: String) {
        wasMovedToChild = true
        command.value = NavigateToUserProfileViewCommand(userId)
    }

    fun setCommentInputVisibility(visibility: Visibility) {
        commentInputVisibilityLiveData.postValue(visibility)
    }

    fun editPost() {
        command.value = StartEditPostViewCommand(model.postId)
    }

    fun onPostMenuClick() {
        val metadata = model.postMetadata
        command.value = ShowPostMenuViewCommand(
            currentUserRepository.authState!!.user.name == postToProcess.userId,
            metadata.version,
            metadata.type)
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

    private fun MediatorLiveData<Boolean>.postLoadingStatus(feedReady: Boolean, postReady: Boolean) {
        postValue(feedReady && postReady && loadingStatusLiveData !is QueryResult.Loading<*>)
    }
}