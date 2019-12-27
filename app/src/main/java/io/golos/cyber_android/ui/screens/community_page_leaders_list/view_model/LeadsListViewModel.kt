package io.golos.cyber_android.ui.screens.community_page_leaders_list.view_model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page_leaders_list.model.LeadsListModel
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view.list.LeadsListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class LeadsListViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: LeadsListModel
) : ViewModelBase<LeadsListModel>(dispatchersProvider, model),
    LeadsListItemEventsProcessor {

    val items: LiveData<List<VersionedListItem>>
        get()  = model.items

    fun onViewCreated() {
        launch {
            model.loadLeaders()
        }
    }

    override fun retry() {
        launch {
            model.retry()
        }
    }
}