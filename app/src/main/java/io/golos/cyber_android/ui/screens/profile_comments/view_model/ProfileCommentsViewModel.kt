package io.golos.cyber_android.ui.screens.profile_comments.view_model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.mappers.mapToComment
import io.golos.cyber_android.ui.mappers.mapToCommentDomain
import io.golos.cyber_android.ui.mappers.mapToContentIdDomain
import io.golos.cyber_android.ui.screens.profile_comments.model.ProfileCommentsModel
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import io.golos.cyber_android.ui.screens.profile_comments.view.view_commands.NavigateToEditComment
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.ui.shared.utils.toBitmapOptions
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.cyber_android.ui.shared.widgets.CommentWidget
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.posts_parsing_rendering.PostGlobalConstants
import io.golos.domain.use_cases.post.post_dto.*
import io.golos.domain.utils.IdUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ProfileCommentsViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: ProfileCommentsModel,
    private val paginator: Paginator.Store<CommentDomain>
) : ViewModelBase<ProfileCommentsModel>(dispatchersProvider, model),
    ProfileCommentsModelEventProcessor {

    override fun onLinkClicked(linkUri: Uri) {
        _command.value = NavigateToLinkViewCommand(linkUri)
    }

    override fun onImageClicked(imageUri: Uri) {
        _command.value = NavigateToImageViewCommand(imageUri)
    }

    override fun onItemClicked(contentId: ContentId) {}

    override fun onUserClicked(userId: String) {
        _command.value = NavigateToUserProfileCommand(UserIdDomain(userId))
    }

    override fun onCommunityClicked(communityId: String) {
        _command.value = NavigateToCommunityPageCommand(communityId)
    }

    override fun onSeeMoreClicked(contentId: ContentId) {}

    private var loadCommentsJob: Job? = null

    private val _commentListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)
    val commentListState = _commentListState.toLiveData()

    init {
        paginator.sideEffectListener = { sideEffect ->
            when (sideEffect) {
                is Paginator.SideEffect.LoadPage -> loadPostComments(sideEffect.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {
                }
            }
        }
        paginator.render = { state ->
            _commentListState.value = state
        }

        loadInitialComments()
    }

    fun onSendComment(commentContent: CommentWidget.CommentContent) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                val contentId = commentContent.contentId
                if (contentId != null) {
                    val commentFromState = getCommentFromStateByContentId(_commentListState.value, contentId)
                    val content = commentContent.message?.let { message ->
                        listOf(ParagraphBlock(null, listOf(TextBlock(IdUtil.generateLongId(), message, null, null))))
                    } ?: listOf()
                    var imageUri = commentContent.imageUri
                    if(imageUri != null && (commentFromState?.body?.attachments?.content?.firstOrNull() as? ImageBlock)?.content != imageUri){
                        //Обновилось изображение и нужно его загрузить на сервер
                        imageUri = Uri.parse(model.uploadAttachmentContent(File(imageUri.toString())))
                    }
                    val attachments = imageUri?.let { uri ->
                        val imageSize = uri.toBitmapOptions()
                        AttachmentsBlock(IdUtil.generateLongId(),
                        listOf(ImageBlock(null,
                            uri,
                            null,
                            imageSize.outWidth,
                            imageSize.outHeight))) }
                    val contentBlock = commentFromState?.body?.copy(
                        content = content,
                        attachments = attachments
                    )
                    val commentForUpdate = commentFromState?.mapToCommentDomain()?.copy(
                        body = contentBlock
                    )
                    commentForUpdate?.let {
                        model.updateComment(it)
                        _commentListState.value = updateCommentAfterEdit(
                            _commentListState.value,
                            contentId,
                            it.body
                        )
                    }
                }
                _command.value = SetLoadingVisibilityCommand(false)
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.unknown_error)
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun editComment(contentId: ContentId) {
        val comment = getCommentFromStateByContentId(_commentListState.value, contentId)
        comment?.let {
            _command.value = NavigateToEditComment(it)
        }
    }

    fun deleteComment(permlink: String, communityId: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.deleteComment(permlink, communityId)
                _commentListState.value = deletePostInState(_commentListState.value, permlink)
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun loadMoreComments() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    override fun onRetryLoadComments() {
        loadInitialComments()
    }

    override fun onCommentLongClick(comment: Comment) {
        _command.value = NavigateToProfileCommentMenuDialogViewCommand(comment)
    }

    override fun onCommentUpVoteClick(commentId: ContentId) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.commentUpVote(commentId.mapToContentIdDomain())
                _commentListState.value = updateUpVoteCountOfVotes(_commentListState.value, commentId)
                _command.value = SetLoadingVisibilityCommand(false)
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.unknown_error)
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onCommentDownVoteClick(commentId: ContentId) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.commentDownVote(commentId.mapToContentIdDomain())
                _commentListState.value = updateDownVoteCountOfVotes(_commentListState.value, commentId)
                _command.value = SetLoadingVisibilityCommand(false)
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.unknown_error)
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    private fun updateUpVoteCountOfVotes(
        state: Paginator.State?,
        contentId: ContentId
    ): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateUpVoteInMessagesByContentId(contentId, comments)
            }
            is Paginator.State.Refresh<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateUpVoteInMessagesByContentId(contentId, comments)
            }
            is Paginator.State.NewPageProgress<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateUpVoteInMessagesByContentId(contentId, comments)
            }
            is Paginator.State.FullData<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateUpVoteInMessagesByContentId(contentId, comments)
            }
        }
        return state
    }


    private fun getCommentFromStateByContentId(state: Paginator.State?, contentId: ContentId): Comment? {
        return when (state) {
            is Paginator.State.Data<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                comments.find { it.comment.contentId == contentId }?.comment
            }
            is Paginator.State.Refresh<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                comments.find { it.comment.contentId == contentId }?.comment
            }
            is Paginator.State.NewPageProgress<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                comments.find { it.comment.contentId == contentId }?.comment
            }
            is Paginator.State.FullData<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                comments.find { it.comment.contentId == contentId }?.comment
            }
            else -> null
        }
    }

    private fun updateUpVoteInMessagesByContentId(contentId: ContentId, comments: ArrayList<ProfileCommentListItem>) {
        val foundedComment = comments.find { comment ->
            comment.comment.contentId == contentId
        }
        val updateCommentItem = foundedComment?.copy()
        updateCommentItem?.let {
            val oldVotes = it.comment.votes
            it.comment = it.comment.copy(
                votes = oldVotes.copy(
                    upCount = oldVotes.upCount + 1,
                    downCount = if (oldVotes.hasDownVote) oldVotes.downCount - 1 else oldVotes.downCount,
                    hasUpVote = true,
                    hasDownVote = false
                )
            )
            comments[comments.indexOf(foundedComment)] = updateCommentItem
        }
    }

    private fun updateDownVoteCountOfVotes(
        state: Paginator.State?,
        contentId: ContentId
    ): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateDownVoteInMessagesByContentId(contentId, comments)
            }
            is Paginator.State.Refresh<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateDownVoteInMessagesByContentId(contentId, comments)
            }
            is Paginator.State.NewPageProgress<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateDownVoteInMessagesByContentId(contentId, comments)
            }
            is Paginator.State.FullData<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateDownVoteInMessagesByContentId(contentId, comments)
            }
        }
        return state
    }

    private fun updateDownVoteInMessagesByContentId(contentId: ContentId, comments: ArrayList<ProfileCommentListItem>) {
        val foundedComment = comments.find { comment ->
            comment.comment.contentId == contentId
        }
        val updateCommentItem = foundedComment?.copy()
        updateCommentItem?.let {
            val oldVotes = it.comment.votes
            it.comment = it.comment.copy(
                votes = oldVotes.copy(
                    downCount = oldVotes.downCount + 1,
                    upCount = if (oldVotes.hasUpVote) oldVotes.upCount - 1 else oldVotes.upCount,
                    hasUpVote = false,
                    hasDownVote = true
                )
            )
            comments[comments.indexOf(foundedComment)] = updateCommentItem
        }
    }

    private fun updateCommentAfterEdit(
        state: Paginator.State?,
        contentId: ContentId,
        body: ContentBlock?
    ): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateCommentByContentId(contentId, comments, body)
            }
            is Paginator.State.Refresh<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateCommentByContentId(contentId, comments, body)
            }
            is Paginator.State.NewPageProgress<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateCommentByContentId(contentId, comments, body)
            }
            is Paginator.State.FullData<*> -> {
                val comments = (state).data as ArrayList<ProfileCommentListItem>
                updateCommentByContentId(contentId, comments, body)
            }
        }
        return state
    }

    private fun updateCommentByContentId(
        contentId: ContentId,
        comments: ArrayList<ProfileCommentListItem>,
        body: ContentBlock?
    ) {
        val foundedComment = comments.find { comment ->
            comment.comment.contentId == contentId
        }
        val updatedCommentItem = foundedComment?.copy()
        updatedCommentItem?.let { item ->
            item.comment = item.comment.copy(body = body)
            comments[comments.indexOf(foundedComment)] = item
        }
    }

    private fun deletePostInState(state: Paginator.State?, permlink: String): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                val comments = (state).data as MutableList<ProfileCommentListItem>
                val foundedComment = comments.find { comment ->
                    comment.comment.contentId.permlink == permlink
                }
                val elementDeletedIndex = comments.indexOf(foundedComment)

                if (elementDeletedIndex != -1 && foundedComment != null) {
                    val updatedComment = foundedComment.comment.copy(
                        body = null,
                        isDeleted = true
                    )

                    comments[elementDeletedIndex] = ProfileCommentListItem(updatedComment)
                }
            }
            is Paginator.State.Refresh<*> -> {
                val comments = (state).data as MutableList<ProfileCommentListItem>
                val foundedComment = comments.find { comment ->
                    comment.comment.contentId.permlink == permlink
                }
                val elementDeletedIndex = comments.indexOf(foundedComment)

                if (elementDeletedIndex != -1 && foundedComment != null) {
                    val updatedComment = foundedComment.comment.copy(
                        body = null,
                        isDeleted = true
                    )

                    comments[elementDeletedIndex] = ProfileCommentListItem(updatedComment)
                }

            }
            is Paginator.State.NewPageProgress<*> -> {
                val comments = (state).data as MutableList<ProfileCommentListItem>
                val foundedComment = comments.find { comment ->
                    comment.comment.contentId.permlink == permlink
                }
                val elementDeletedIndex = comments.indexOf(foundedComment)

                if (elementDeletedIndex != -1 && foundedComment != null) {
                    val updatedComment = foundedComment.comment.copy(
                        body = null,
                        isDeleted = true
                    )

                    comments[elementDeletedIndex] = ProfileCommentListItem(updatedComment)
                }

            }
            is Paginator.State.FullData<*> -> {
                val comments = (state).data as MutableList<ProfileCommentListItem>
                val foundedComment = comments.find { comment ->
                    comment.comment.contentId.permlink == permlink
                }
                val elementDeletedIndex = comments.indexOf(foundedComment)

                if (elementDeletedIndex != -1 && foundedComment != null) {
                    val updatedComment = foundedComment.comment.copy(
                        body = null,
                        isDeleted = true
                    )

                    comments[elementDeletedIndex] = ProfileCommentListItem(updatedComment)
                }
            }
        }
        return state
    }

    private fun loadInitialComments() {
        val postsListState = _commentListState.value
        if (postsListState is Paginator.State.Empty || postsListState is Paginator.State.EmptyError) {
            restartLoadComments()
        }
    }

    private fun restartLoadComments() {
        loadCommentsJob?.cancel()
        paginator.proceed(Paginator.Action.Restart)
    }

    private fun loadPostComments(pageCount: Int) {
        loadCommentsJob = launch {
            try {
                val commentsDomain = model.getComments(
                    offset = pageCount * PAGINATION_PAGE_SIZE,
                    pageSize = PAGINATION_PAGE_SIZE
                ).map { it.mapToComment() }
                    .map { ProfileCommentListItem(it) }
                launch(Dispatchers.Main) {
                    paginator.proceed(Paginator.Action.NewPage(pageCount, commentsDomain))
                }
            } catch (e: Exception) {
                Timber.e(e)
                paginator.proceed(Paginator.Action.PageError(e))
            }
        }
    }

    override fun onCleared() {
        loadCommentsJob?.cancel()
        super.onCleared()
    }

}