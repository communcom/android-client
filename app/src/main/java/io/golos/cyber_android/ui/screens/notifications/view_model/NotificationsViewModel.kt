package io.golos.cyber_android.ui.screens.notifications.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.mappers.mapToUser
import io.golos.cyber_android.ui.screens.notifications.mappers.mapToVersionedListItem
import io.golos.cyber_android.ui.screens.notifications.model.NotificationsModel
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.SubscribeNotificationItem
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToPostCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToWalletCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

class NotificationsViewModel
@Inject constructor(
    private val appContext: Context,
    notificationsModel: NotificationsModel,
    dispatchersProvider: DispatchersProvider,
    private val paginator: Paginator.Store<VersionedListItem>
) : ViewModelBase<NotificationsModel>(dispatchersProvider, notificationsModel),
    NotificationsViewModelListEventsProcessor {

    private val _notificationsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)
    val notificationsListState = _notificationsListState.toLiveData()

    private val _newNotificationsCount: MutableLiveData<Int> = MutableLiveData(0)
    val newNotificationsCount = _newNotificationsCount.toLiveData()

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing get() = _swipeRefreshing.toLiveData()

    private var loadCommentsJob: Job? = null

    private lateinit var currentUser: User

    private var isPendingMarkNotifications: Boolean = false

    private var isPendingReloadNotifications: Boolean = false

    private var isVisible by Delegates.observable(false) { _, oldVisibleStatus, newVisibleStatus ->
        if(oldVisibleStatus != newVisibleStatus){
            if(newVisibleStatus){
                if(isPendingMarkNotifications){
                    //Need mark notifications as read when notifications is visible on screen user
                    val notifications = paginator.getStoredItems() as MutableList<BaseNotificationItem>
                    notifications.firstOrNull()?.let {
                        markAllNotificationsAsShowed(it.createTime)
                    }
                }
            } else{
                if(isPendingReloadNotifications){
                    restartLoadNotifications()
                }
            }
        }
    }

    init {
        paginator.pageSize = PAGINATION_PAGE_SIZE
        paginator.sideEffectListener = { sideEffect ->
            when (sideEffect) {
                is Paginator.SideEffect.LoadPage -> loadNotifications(sideEffect.pageCount, sideEffect.pageKey)
                is Paginator.SideEffect.ErrorEvent -> {
                }
            }
        }
        paginator.render = { newState, _ ->
            _notificationsListState.value = newState
        }
        subscribeToNewNotificationsChanges()
        loadNotificationsFirstPage()
    }

    fun onSwipeRefresh() {
        launch {
            paginator.initState(Paginator.State.Empty)
            restartLoadNotifications()

            _swipeRefreshing.value = false
        }
    }

    fun onActive() {
        // Reload the list when screen is active
        launch {
            paginator.initState(Paginator.State.Empty)
            restartLoadNotifications()
        }
    }

    private fun subscribeToNewNotificationsChanges() {
        launch {
            model.geNewNotificationsCounterFlow()
                .distinctUntilChanged()
                .debounce(DEBOUNCE_INTERVAL_UPDATE_NOTIFICATIONS_IN_MILLIS)
                .collect {
                if (it.newNotificationsCounter != _newNotificationsCount.value && it.lastNotificationDateMarked == null) {
                    //Update from backend
                    restartLoadNotifications()
                }
            }
        }
    }

    override fun onUserClickedById(userId: UserIdDomain) {
        if(currentUser.id != userId.userId){
            _command.value = NavigateToUserProfileCommand(userId)
        }
    }

    override fun onPostNavigateClicked(contentId: ContentIdDomain) {
        val discussionIdModel = DiscussionIdModel(contentId.userId, Permlink(contentId.permlink))
        _command.value = NavigateToPostCommand(discussionIdModel, contentId)
    }

    override fun onWalletNavigateClicked() {
        launch {
            try {
                _command.value = NavigateToWalletCommand(model.getBalance())
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageTextCommand(ex.getMessage(appContext))
            }
        }
    }

    override fun onChangeFollowerStatusClicked(notification: BaseNotificationItem) {
        if(notification is SubscribeNotificationItem){

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
            restartLoadNotifications()
        }
    }

    private fun restartLoadNotifications() {
        loadCommentsJob?.cancel()
        paginator.proceed(Paginator.Action.Restart)
    }

    private fun loadNotifications(pageCount: Int, pageKey: String?){
        loadCommentsJob = launch {
            try {
                isPendingReloadNotifications = false
                val notificationsPage = model.getNotifications(pageKey, PAGINATION_PAGE_SIZE)
                val notifications = notificationsPage.notifications
                var notificationsUnreadCount = _newNotificationsCount.value
                if(pageCount == 0){
                    currentUser = model.getCurrentUser().mapToUser()
                    //Need load unread count
                    notificationsUnreadCount = model.getNewNotificationsCounter()
                    if(notificationsUnreadCount != 0){
                        if(isVisible){
                            notifications.firstOrNull()?.let {
                                markAllNotificationsAsShowed(it.createTime)
                            }
                        } else{
                            isPendingMarkNotifications = true
                        }
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

    fun onVisibilityChanged(visible: Boolean, changeStackPage: Boolean) {
        if(!changeStackPage){
            isVisible = visible
        }
    }

    private fun markAllNotificationsAsShowed(date: Date){
        launch {
            try{
                model.markAllNotificationAsViewed(date)
                isPendingMarkNotifications = false
                if(isVisible){
                    isPendingReloadNotifications = true
                } else{
                    restartLoadNotifications()
                }
            } catch (e: Exception){
                Timber.e(e)
            }
        }
    }

    private companion object{

        private const val DEBOUNCE_INTERVAL_UPDATE_NOTIFICATIONS_IN_MILLIS = 500L
    }
}