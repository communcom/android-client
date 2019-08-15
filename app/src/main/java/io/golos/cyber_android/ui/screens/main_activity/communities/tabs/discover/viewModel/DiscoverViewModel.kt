package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.viewModel

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
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

    val isScrollEnabled: MutableLiveData<Boolean> = MutableLiveData(false)

    fun onViewCreated() {
        isSetup = false
    }

    fun onActive(controlHeight: Int) {
        model.initModel(controlHeight)

        if(!isSetup) {
            loadPage(0)
            isSetup = true
        }
    }

    fun onScroll(lastVisibleItemPosition: Int) {
        if(!model.canLoad(lastVisibleItemPosition)) {
            return
        }

        isScrollEnabled.value = false
        loadPage(lastVisibleItemPosition)
    }

    private fun loadPage(lastVisibleItemPosition: Int) {
        launch {
            val page = model.getPage(lastVisibleItemPosition)

            page.fold({ pageInfo ->
                pageInfo.data?.let { list -> items.value = list }

                if(pageInfo.hasNextData) {
                    loadPage(lastVisibleItemPosition)
                } else {
                    isScrollEnabled.value = true
                }
            }, {
                command.value = ShowMessageCommand(R.string.common_general_error)
                it.data?.let { list -> items.value = list }
                isScrollEnabled.value = true
            })
        }
    }
}