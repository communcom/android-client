package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.viewModel

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.model.DiscoverModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.list.CommunityListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import io.golos.domain.extensions.fold
import kotlinx.coroutines.launch
import javax.inject.Inject

class DiscoverViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: DiscoverModel
) : ViewModelBase<DiscoverModel>(dispatchersProvider, model), CommunityListItemEventsProcessor {

    private var isSetup = false

    val isSearchResultVisible: MutableLiveData<Boolean> = MutableLiveData(false)

    val items: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())

    fun onActive() {
        if(!isSetup) {
            loadFirstPage()
            isSetup = true
        }
    }

    private fun loadFirstPage() {
        command.value = SetLoadingVisibilityCommand(true)

        launch {
            val firstPageItems = model.getFirstPage()

            command.value = SetLoadingVisibilityCommand(false)

            firstPageItems.fold({
                items.value = it
            }, {
                command.value = ShowMessageCommand(R.string.common_general_error)
            })
        }
    }
}