package io.golos.cyber_android.ui.screens.main_activity.communities.view_model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.model.CommunitiesModel
import io.golos.cyber_android.ui.screens.main_activity.communities.view.list.CommunityListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommunitiesViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunitiesModel
) : ViewModelBase<CommunitiesModel>(dispatchersProvider, model), CommunityListItemEventsProcessor {

    val items: LiveData<List<VersionedListItem>>
        get()  = model.items

    val pageSize: Int = model.pageSize

    fun onViewCreated() = loadPage()

    override fun onItemClick(community: CommunityDomain) {
        commandMutableLiveData.value = NavigateToCommunityPageCommand(community.communityId)
    }

    override fun onNextPageReached() = loadPage()

    private fun loadPage() {
        launch {
            model.loadPage()
        }
    }
}