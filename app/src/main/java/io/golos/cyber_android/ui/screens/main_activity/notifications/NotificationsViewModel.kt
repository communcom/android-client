package io.golos.cyber_android.ui.screens.main_activity.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.domain.use_cases.model.UpdateOption
import io.golos.domain.use_cases.notifs.events.EventsUseCase
import io.golos.domain.extensions.map
import io.golos.domain.requestmodel.EventModel
import io.golos.domain.requestmodel.QueryResult
import javax.inject.Inject


class NotificationsViewModel
@Inject
constructor(
    private val eventsUseCase: EventsUseCase
) : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    /**
     * [LiveData] that indicates if data is loading
     */
    val loadingStatusLiveData = eventsUseCase.getUpdatingState.map {
        it is QueryResult.Loading
    }

    val readinessLiveData = eventsUseCase.getReadinessLiveData

    /**
     * [LiveData] there was an error during loading¡
     */
    val errorStatusLiveData = eventsUseCase.getUpdatingState.map {
        it is QueryResult.Error
    }

    /**
     * [LiveData] that indicates if last page was reached
     */
    val lastPageLiveData = eventsUseCase.getLastFetchedChunk.map{
        (it?.size ?: 0) % PAGE_SIZE != 0 || it?.isEmpty() == true
    }


    /**
     * [LiveData] of all the [EventModel] items
     */
    val feedLiveData = eventsUseCase.getAsLiveData

    init {
        eventsUseCase.subscribe()
        requestRefresh()
    }

    /**
     * Requests full refresh of the data. New data can be listened by [feedLiveData]
     */
    fun requestRefresh() {
        eventsUseCase.requestUpdate(PAGE_SIZE, UpdateOption.REFRESH_FROM_BEGINNING)
    }

    /**
     * Requests new page of the data. New data can be listened by [feedLiveData]
     */
    fun loadMore() {
        eventsUseCase.requestUpdate(PAGE_SIZE, UpdateOption.FETCH_NEXT_PAGE)
    }

    override fun onCleared() {
        super.onCleared()
        eventsUseCase.unsubscribe()
    }
}