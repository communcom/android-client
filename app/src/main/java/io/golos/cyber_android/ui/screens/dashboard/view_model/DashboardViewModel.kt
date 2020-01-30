package io.golos.cyber_android.ui.screens.dashboard.view_model

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.screens.dashboard.model.DashboardModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.cyber_android.ui.shared.widgets.NavigationBottomMenuWidget
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DashboardViewModel
@Inject
constructor(
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
                _newNotificationsCounter.value = it
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

    private fun onTabSelected(tab: NavigationBottomMenuWidget.Tab) {
        _currentTabLiveData.postValue(tab)
    }

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
}