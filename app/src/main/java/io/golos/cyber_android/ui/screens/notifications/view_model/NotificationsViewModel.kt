package io.golos.cyber_android.ui.screens.notifications.view_model

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.notifications.mappers.mapToVersionedListItem
import io.golos.cyber_android.ui.screens.notifications.model.NotificationsModel
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationSubscribeItem
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class NotificationsViewModel @Inject constructor(notificationsModel: NotificationsModel,
                                                 dispatchersProvider: DispatchersProvider,
                                                 private val paginator: Paginator.Store<VersionedListItem>) : ViewModelBase<NotificationsModel>(dispatchersProvider, notificationsModel),
    NotificationsViewModelListEventsProcessor {

    private val _notificationsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)
    val notificationsListState = _notificationsListState.toLiveData()

    private val _newNotificationsCount: MutableLiveData<Int> = MutableLiveData(0)
    val newNotificationsCount = _newNotificationsCount.toLiveData()

    private var loadCommentsJob: Job? = null

    init {
        paginator.sideEffectListener = { sideEffect ->
            when (sideEffect) {
                is Paginator.SideEffect.LoadPage -> loadNotifications(sideEffect.pageCount, sideEffect.pageKey)
                is Paginator.SideEffect.ErrorEvent -> {
                }
            }
        }
        paginator.render = { state ->
            _notificationsListState.value = state
        }

        loadNotificationsFirstPage()
    }

    override fun onUserClicked(userId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChangeFollowerStatusClicked(notification: BaseNotificationItem) {
        if(notification is NotificationSubscribeItem){

        }
    }


    override fun loadMoreNotifications() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    override fun onRetryLoadPage() {
        if(_notificationsListState.value is Paginator.State.PageError<*>){
            paginator.proceed(Paginator.Action.LoadMore)
        } else{
            paginator.proceed(Paginator.Action.Restart)
        }
    }

    private fun loadNotificationsFirstPage(){
        val postsListState = _notificationsListState.value
        if (postsListState is Paginator.State.Empty || postsListState is Paginator.State.EmptyError) {
            restartLoadComments()
        }
    }

    private fun restartLoadComments() {
        loadCommentsJob?.cancel()
        paginator.proceed(Paginator.Action.Restart)
    }

    private fun loadNotifications(pageCount: Int, pageKey: String?){
        loadCommentsJob?.cancel()
        loadCommentsJob = launch {
            try {
                val notifications = model.getNotifications(pageKey, PAGINATION_PAGE_SIZE)
                var notificationsUnreadCount = _newNotificationsCount.value
                if(pageCount == 0){
                    //Need load unread count
                    notificationsUnreadCount = 10
                    //Need mark notifications as read
                }
                val notificationItems = notifications.map { it.mapToVersionedListItem() }
                val newPageKey = notifications.firstOrNull()?.lastNotificationTime
                launch(Dispatchers.Main) {
                    paginator.proceed(Paginator.Action.NewPage(pageCount, notificationItems, newPageKey))
                    _newNotificationsCount.value = notificationsUnreadCount
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