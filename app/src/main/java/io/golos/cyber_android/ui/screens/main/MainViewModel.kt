package io.golos.cyber_android.ui.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.interactors.notifs.events.EventsUseCase
import io.golos.domain.interactors.sign.SignInUseCase

class MainViewModel(private val signInUseCase: SignInUseCase,
                    private val eventsUseCase: EventsUseCase
): ViewModel() {

    val unreadNotificationsLiveData = eventsUseCase.getUnreadLiveData

    /**
     * [LiveData] that indicates current state of auth process
     */
    val authStateLiveData = signInUseCase.getAsLiveData.asEvent()

    init {
        signInUseCase.subscribe()
        eventsUseCase.subscribe()
        eventsUseCase.requestUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)
    }

    override fun onCleared() {
        super.onCleared()
        signInUseCase.unsubscribe()
        eventsUseCase.unsubscribe()
    }
}