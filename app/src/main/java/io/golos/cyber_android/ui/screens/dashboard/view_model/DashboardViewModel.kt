package io.golos.cyber_android.ui.screens.dashboard.view_model

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.dashboard.dto.DeepLinkInfo
import io.golos.cyber_android.ui.screens.dashboard.dto.OpenNotificationInfo
import io.golos.cyber_android.ui.screens.dashboard.model.DashboardModel
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.utils.IntentConstants
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.cyber_android.ui.shared.widgets.NavigationBottomMenuWidget
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DashboardViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    dashboardModel: DashboardModel,
    private val currentUserRepository: CurrentUserRepositoryRead
) : ViewModelBase<DashboardModel>(dispatchersProvider, dashboardModel),
    NavigationBottomMenuWidget.Listener{

    private val _currentTabLiveData = MutableLiveData(NavigationBottomMenuWidget.Tab.FEED)

    /**
     * Currently selected tab of a main screen
     */
    val currentTabLiveData = _currentTabLiveData.toLiveData()

    private val _newNotificationsCounter = MutableLiveData<Int>()

    val newNotificationsCounter = _newNotificationsCounter.toLiveData()

    private val _createTabLiveData = MutableLiveData<Any>()

    val createTabLiveData = _createTabLiveData

    val currentUser: UserIdDomain
        get() = currentUserRepository.userId

    private val mediator = MediatorLiveData<Any>()
    private val observer = Observer<Any> {}

    init {
        mediator.observeForever(observer)
        launch {
            model.getNewNotificationsCounterFlow()
                .distinctUntilChanged()
                .collect {
                _newNotificationsCounter.value = it.newNotificationsCounter
            }
        }
    }

    override fun onFeedClick(tab: NavigationBottomMenuWidget.Tab) = onTabSelected(tab)

    override fun onCommunityClick(tab: NavigationBottomMenuWidget.Tab) = onTabSelected(tab)

    override fun onCreateClick() {
        _createTabLiveData.postValue(Any())
    }

    override fun onNotificationClick(tab: NavigationBottomMenuWidget.Tab) = onTabSelected(tab)

    override fun onProfileClick(tab: NavigationBottomMenuWidget.Tab) = onTabSelected(tab)

    override fun onCleared() {
        super.onCleared()
        mediator.removeObserver(observer)
    }

    fun updateUnreadNotificationsCounter() {
        launch {
            try {
                model.updateNewNotificationsCounter()
            } catch (e: Exception){
                Timber.e(e)
            }
        }
    }

    fun processIntent(intent: Intent) =
        when(intent.action) {
            Intent.ACTION_VIEW -> processDeepLink(intent)
            IntentConstants.ACTION_OPEN_NOTIFICATION -> processNotification(intent)
            else -> Timber.w("This intent is not supported: ${intent.action}")
        }

    private fun processDeepLink(intent: Intent) {
        launch {
            model.parseDeepLinkUri(intent.data!!)
                ?.let { linkInfo ->
                    _command.value = when(linkInfo) {
                        is DeepLinkInfo.ProfileDeepLink -> {
                            NavigateToUserProfileCommand(linkInfo.userId)
                        }
                        is DeepLinkInfo.CommunityDeepLink -> {
                            NavigateToCommunityPageCommand(linkInfo.communityId)
                        }
                        is DeepLinkInfo.PostDeepLink -> {
                            val discussionId = DiscussionIdModel(linkInfo.userId.userId, Permlink(linkInfo.postId))
                            val contentId = ContentId(linkInfo.communityId, linkInfo.postId, linkInfo.userId.userId)
                            NavigateToPostCommand(discussionId, contentId)
                        }
                    }
                }
        }
    }

    private fun processNotification(intent: Intent) {
        launch {
            try {
                model.parseOpenNotification(intent)
                    ?.let {
                        _command.value = when(it) {
                            is OpenNotificationInfo.OpenProfile -> {
                                NavigateToUserProfileCommand(it.userId)
                            }
                            is OpenNotificationInfo.OpenPost -> {
                                val discussionId = DiscussionIdModel(it.contentId.userId, Permlink(it.contentId.permlink))
                                NavigateToPostCommand(discussionId, it.contentId)
                            }
                            is OpenNotificationInfo.OpenWallet -> {
                                NavigateToWalletCommand(it.balance)
                            }
                        }
                    }
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageTextCommand(ex.getMessage(appContext))
            }
        }
    }

    private fun onTabSelected(tab: NavigationBottomMenuWidget.Tab) {
        _currentTabLiveData.postValue(tab)
    }
}