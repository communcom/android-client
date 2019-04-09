package io.golos.cyber_android.ui.screens.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FeedPageViewModel: ViewModel() {

    sealed class Event {
        data class SearchEvent(val query: String): Event()
    }

    private val eventsLiveData = MutableLiveData<Event>()

    val getEventsLiveData = eventsLiveData as LiveData<Event>

    fun onSearch(query: String) {
        eventsLiveData.postValue(Event.SearchEvent(query))
    }
}