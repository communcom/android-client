package io.golos.cyber_android.ui.screens.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FeedPageViewModel: ViewModel() {

    sealed class Event {
        data class SearchEvent(val query: String): Event()
    }

    val eventsLiveData = MutableLiveData<Event>()

    fun onSearch(query: String) {
        eventsLiveData.postValue(Event.SearchEvent(query))
    }
}