package io.golos.cyber_android.ui.screens.profile_comments.view_model

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.screens.profile_comments.model.ProfileCommentsModel
import io.golos.cyber_android.ui.screens.profile_comments.model.ProfileCommentsModelEventProcessor
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import io.golos.cyber_android.ui.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.ui.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommentDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ProfileCommentsViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: ProfileCommentsModel,
    private val paginator: Paginator.Store<CommentDomain>
) : ViewModelBase<ProfileCommentsModel>(dispatchersProvider, model), ProfileCommentsModelEventProcessor {

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

    fun loadMoreComments() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    override fun onRetryLoadComments() {
        loadInitialComments()
    }

    override fun onCommentUpVoteClick(commentId: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.upVote(commentId)
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onCommentDownVoteClick(commentId: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.downVote(commentId)
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
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
                ).map { ProfileCommentListItem(it) }
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