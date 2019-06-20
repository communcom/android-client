package io.golos.cyber_android.ui.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.interactors.notifs.events.EventsUseCase
import io.golos.domain.interactors.sign.SignInUseCase

class MainViewModel(
    private val signInUseCase: SignInUseCase,
    private val eventsUseCase: EventsUseCase) : ViewModel() {

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

    init {
        signInUseCase.subscribe()
        eventsUseCase.subscribe()
        eventsUseCase.requestUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)
    }


    fun onTabSelected(tab: MainActivity.Tab) {
        currentTabLiveData.postValue(tab)
    }

    override fun onCleared() {
        super.onCleared()
        signInUseCase.unsubscribe()
        eventsUseCase.unsubscribe()
    }
}