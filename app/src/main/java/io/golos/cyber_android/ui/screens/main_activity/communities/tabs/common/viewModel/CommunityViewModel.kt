package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.viewModel

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.CommunityModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list.CommunityListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommunityViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityModel
) : ViewModelBase<CommunityModel>(dispatchersProvider, model), CommunityListItemEventsProcessor {

    private var isSetup = false

    val items: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())

    val isScrollEnabled: MutableLiveData<Boolean> = MutableLiveData(false)

    val searchResultVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val searchResultItems: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())

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

    override fun onItemClick(community: CommunityDomain) {
        commandMutableLiveData.value = NavigateToCommunityPageCommand(community.communityId)
    }

    private fun loadPage(lastVisibleItemPosition: Int) {
        launch {
            try {
                val page = model.getPage(lastVisibleItemPosition)

                page.data?.let { list -> items.value = list }

                if(page.hasNextData) {
                    loadPage(lastVisibleItemPosition)
                } else {
                    isScrollEnabled.value = true
                }
            } catch (ex: Exception) {
                commandMutableLiveData.value = ShowMessageCommand(R.string.common_general_error)
                isScrollEnabled.value = true
            }
        }
    }
}