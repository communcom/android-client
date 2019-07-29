package io.golos.cyber_android.ui.screens.main_activity

import androidx.lifecycle.*
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.interactors.notifs.events.EventsUseCase
import io.golos.domain.interactors.notifs.push.PushNotificationsSettingsUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.requestmodel.PushNotificationsStateModel
import io.golos.domain.requestmodel.QueryResult
import javax.inject.Inject

class MainViewModel
@Inject
constructor(
    private val signInUseCase: SignInUseCase,
    private val eventsUseCase: EventsUseCase,
    private val pushesUseCase: PushNotificationsSettingsUseCase
) : ViewModel() {

    private val currentTabLiveData = MutableLiveData(MainActivity.Tab.FEED)

    /**
     * Currently selected tab of a main screen
     */
    val getCurrentTabLiveData = currentTabLiveData as LiveData<MainActivity.Tab>

    val unreadNotificationsLiveData = eventsUseCase.getUnreadLiveData

    /**
     * [LiveData] that indicates current state of auth process
     */
    val authStateLiveData = signInUseCase.getAsLiveData

    /**
     * When user is logged in and his push notifications is enabled we need to send fcm token and device id
     * to backend. This will happen on every app run just once.
     */
    private val pushesSettingsObserver = Observer<QueryResult<PushNotificationsStateModel>> {
        if (it is QueryResult.Success) {
            if (it.originalQuery.isEnabled)
                pushesUseCase.subscribeToPushNotifications()
            mediator.removeObserver(observer)
        }
    }

    private val mediator = MediatorLiveData<Any>()
    private val observer = Observer<Any> {}

    init {
        signInUseCase.subscribe()
        eventsUseCase.subscribe()
        pushesUseCase.subscribe()
        eventsUseCase.requestUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)

        mediator.addSource(pushesUseCase.getAsLiveData, pushesSettingsObserver)
        mediator.observeForever(observer)
    }


    fun onTabSelected(tab: MainActivity.Tab) {
        currentTabLiveData.postValue(tab)
    }

    override fun onCleared() {
        super.onCleared()
        signInUseCase.unsubscribe()
        eventsUseCase.unsubscribe()
        pushesUseCase.unsubscribe()
        mediator.removeSource(pushesUseCase.getAsLiveData)
        mediator.removeObserver(observer)
    }
}