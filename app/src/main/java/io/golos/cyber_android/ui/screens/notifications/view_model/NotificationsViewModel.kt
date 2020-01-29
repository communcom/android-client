package io.golos.cyber_android.ui.screens.notifications.view_model

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.mappers.mapToUser
import io.golos.cyber_android.ui.screens.notifications.mappers.mapToVersionedListItem
import io.golos.cyber_android.ui.screens.notifications.model.NotificationsModel
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationSubscribeItem
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToPostCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
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

    private lateinit var currentUser: User

    init {
        paginator.pageSize = PAGINATION_PAGE_SIZE
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
        subscribeToNewNotificationsChanges()
        loadNotificationsFirstPage()
    }

    private fun subscribeToNewNotificationsChanges(){
        launch {
            model.geNewNotificationsCounterFlow().collect {
                if(it != _newNotificationsCount.value){
                    restartLoadComments()
                }
            }
        }
    }

    override fun onUserClickedById(userId: UserIdDomain) {
        if(currentUser.id != userId.userId){
            _command.value = NavigateToUserProfileCommand(userId)
        }
    }

    override fun onPostNavigateClicked(contentId: ContentId) {
        val discussionIdModel = DiscussionIdModel(contentId.userId, Permlink(contentId.permlink))
        _command.value = NavigateToPostCommand(discussionIdModel, contentId)
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
        paginator.proceed(Paginator.Action.Restart)
    }

    private fun loadNotifications(pageCount: Int, pageKey: String?){
        loadCommentsJob?.cancel()
        loadCommentsJob = launch {
            try {
                val notificationsPage = model.getNotifications(pageKey, PAGINATION_PAGE_SIZE)
                val notifications = notificationsPage.notifications
                var notificationsUnreadCount = _newNotificationsCount.value
                if(pageCount == 0){
                    currentUser = model.getCurrentUser().mapToUser()

                    //Need load unread count
                    notificationsUnreadCount = model.getNewNotificationsCounter()
                    //Need mark notifications as read
                    notifications.firstOrNull()?.let {
                        model.markAllNotificationAsViewed(it.createTime)
                    }
                }
                val notificationItems = notifications.map { it.mapToVersionedListItem() }
                val newPageKey = notificationsPage.lastNotificationTimeStamp
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