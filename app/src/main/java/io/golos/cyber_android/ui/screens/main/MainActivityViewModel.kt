package io.golos.cyber_android.ui.screens.main

import androidx.lifecycle.ViewModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.interactors.notifs.events.EventsUseCase

class MainActivityViewModel(private val eventsUseCase: EventsUseCase): ViewModel() {


    val unreadNotificationsLiveData = eventsUseCase.getUnreadLiveData

    init {
        eventsUseCase.subscribe()
        eventsUseCase.requestUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)
    }

    override fun onCleared() {
        super.onCleared()
        eventsUseCase.unsubscribe()
    }
}