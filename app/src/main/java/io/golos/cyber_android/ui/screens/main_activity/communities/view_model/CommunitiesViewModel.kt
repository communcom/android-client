package io.golos.cyber_android.ui.screens.main_activity.communities.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.recycler_view.ListItem
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

    private var isSetup = false

    private val _items: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())
    val items: LiveData<List<ListItem>>
        get()  = _items

    private val _isScrollEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val isScrollEnabled: LiveData<Boolean>
        get() = _isScrollEnabled

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

        _isScrollEnabled.value = false
        loadPage(lastVisibleItemPosition)
    }

    override fun onItemClick(community: CommunityDomain) {
        commandMutableLiveData.value = NavigateToCommunityPageCommand(community.communityId)
    }

    private fun loadPage(lastVisibleItemPosition: Int) {
        launch {
            try {
                val page = model.getPage(lastVisibleItemPosition)

                page.data?.let { list -> _items.value = list }

                if(page.hasNextData) {
                    loadPage(lastVisibleItemPosition)
                } else {
                    _isScrollEnabled.value = true
                }
            } catch (ex: Exception) {
                commandMutableLiveData.value = ShowMessageCommand(R.string.common_general_error)
                _isScrollEnabled.value = true
            }
        }
    }
}