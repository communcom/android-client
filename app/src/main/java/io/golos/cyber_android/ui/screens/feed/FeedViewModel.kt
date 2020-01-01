package io.golos.cyber_android.ui.screens.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowPostFiltersCommand
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider
) : ViewModelBase<ModelBase>(dispatchersProvider) {

    private val eventsLiveData = MutableLiveData<Event>()

    val getEventsLiveData = eventsLiveData as LiveData<Event>

    fun onFiltersCLicked() {
        _command.value = ShowPostFiltersCommand()
    }

    sealed class Event {
        data class SearchEvent(val query: String) : Event()
        object RefreshRequestEvent : Event()
    }


}