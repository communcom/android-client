package io.golos.cyber_android.ui.screens.notifications.view_model

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.notifications.model.NotificationsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class NotificationsViewModel @Inject constructor(notificationsModel: NotificationsModel,
                                                 dispatchersProvider: DispatchersProvider,
                                                 private val paginator: Paginator.Store<VersionedListItem>) : ViewModelBase<NotificationsModel>(dispatchersProvider, notificationsModel) {

    private val _commentListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)
    val commentListState = _commentListState.toLiveData()

    private var loadCommentsJob: Job? = null

    init {
        paginator.sideEffectListener = { sideEffect ->
            when (sideEffect) {
                is Paginator.SideEffect.LoadPage -> loadNotifications(sideEffect.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {
                }
            }
        }
        paginator.render = { state ->
            _commentListState.value = state
        }

        loadNotifications(0)
    }


    fun loadMoreComments() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    private fun loadNotificationsFirstPage(){
        val postsListState = _commentListState.value
        if (postsListState is Paginator.State.Empty || postsListState is Paginator.State.EmptyError) {
            restartLoadComments()
        }
    }

    private fun restartLoadComments() {
        loadCommentsJob?.cancel()
        paginator.proceed(Paginator.Action.Restart)
    }

    private fun loadNotifications(pageCount: Int){
        loadCommentsJob?.cancel()
        loadCommentsJob = launch {
            try {
                /*val commentsDomain = model.getNotifications(PAGINATION_PAGE_SIZE
                ).map { it.mapToComment() }
                    .map { ProfileCommentListItem(it) }
                launch(Dispatchers.Main) {
                    paginator.proceed(Paginator.Action.NewPage(pageCount, commentsDomain))
                }*/
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