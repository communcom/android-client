package io.golos.cyber_android.ui.screens.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowPostFiltersCommand
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    model: PostFiltersModel,
    dispatchersProvider: DispatchersProvider
) : ViewModelBase<PostFiltersModel>(dispatchersProvider, model) {

    private val eventsLiveData = MutableLiveData<Event>()

    val getEventsLiveData = eventsLiveData as LiveData<Event>

    fun onFiltersCLicked() {
        _command.value = ShowPostFiltersCommand()
    }

    fun onChangeTabFilter(currentFeed: PostFiltersHolder.CurrentOpenTypeFeed) {
        launch {
            model.updateOpenFeedType(currentFeed)
        }
    }

    sealed class Event {
        data class SearchEvent(val query: String) : Event()
        object RefreshRequestEvent : Event()
    }


}