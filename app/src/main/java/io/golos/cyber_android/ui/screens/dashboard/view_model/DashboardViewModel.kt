package io.golos.cyber_android.ui.screens.dashboard.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.widgets.NavigationBottomMenuWidget
import io.golos.cyber_android.ui.screens.dashboard.model.DashboardModel
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.model.UpdateOption
import io.golos.domain.use_cases.notifs.events.EventsUseCase
import io.golos.domain.use_cases.sign.SignInUseCase
import javax.inject.Inject

class DashboardViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    dashboardModel: DashboardModel,
    private val signInUseCase: SignInUseCase,
    private val eventsUseCase: EventsUseCase,
    private val currentUserRepository: CurrentUserRepositoryRead
) : ViewModelBase<DashboardModel>(dispatchersProvider, dashboardModel),
    NavigationBottomMenuWidget.Listener{

    private val currentTabLiveData = MutableLiveData(NavigationBottomMenuWidget.Tab.FEED)

    /**
     * Currently selected tab of a main screen
     */
    val getCurrentTabLiveData = currentTabLiveData as LiveData<NavigationBottomMenuWidget.Tab>

    private val _createTabLiveData = MutableLiveData<Any>()

    val createTabLiveData = _createTabLiveData

    val unreadNotificationsLiveData = eventsUseCase.getUnreadLiveData

    val currentUser: UserIdDomain
        get() = currentUserRepository.userId

    private val mediator = MediatorLiveData<Any>()
    private val observer = Observer<Any> {}

    init {
        signInUseCase.subscribe()
        eventsUseCase.subscribe()
        eventsUseCase.requestUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)

        mediator.observeForever(observer)
    }

    override fun onFeedClick(tab: NavigationBottomMenuWidget.Tab) = onTabSelected(tab)

    override fun onCommunityClick(tab: NavigationBottomMenuWidget.Tab) = onTabSelected(tab)

    override fun onCreateClick() {
        _createTabLiveData.postValue(Any())
    }

    override fun onNotificationClick(tab: NavigationBottomMenuWidget.Tab) = onTabSelected(tab)

    override fun onProfileClick(tab: NavigationBottomMenuWidget.Tab) = onTabSelected(tab)

    private fun onTabSelected(tab: NavigationBottomMenuWidget.Tab) {
        currentTabLiveData.postValue(tab)
    }

    override fun onCleared() {
        super.onCleared()
        signInUseCase.unsubscribe()
        eventsUseCase.unsubscribe()
        mediator.removeObserver(observer)
    }
}