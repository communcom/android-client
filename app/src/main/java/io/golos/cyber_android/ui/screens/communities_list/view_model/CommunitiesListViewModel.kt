package io.golos.cyber_android.ui.screens.communities_list.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModel
import io.golos.cyber_android.ui.screens.communities_list.view.list.CommunityListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityDomain
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class CommunitiesListViewModel
@Inject
constructor(
    @Named(Clarification.BACK_BUTTON)
    isBackButtonVisible: Boolean,
    dispatchersProvider: DispatchersProvider,
    model: CommunitiesListModel
) : ViewModelBase<CommunitiesListModel>(dispatchersProvider, model), CommunityListItemEventsProcessor {

    private val _backButtonVisibility = MutableLiveData<Int>(if(isBackButtonVisible) View.VISIBLE else View.INVISIBLE)
    val backButtonVisibility: LiveData<Int> get() = _backButtonVisibility

    val items: LiveData<List<VersionedListItem>>
        get()  = model.items

    val pageSize: Int = model.pageSize

    fun onViewCreated() = loadPage()

    override fun onItemClick(community: CommunityDomain) {
        _command.value = NavigateToCommunityPageCommand(community.communityId)
    }

    override fun onNextPageReached() = loadPage()

    override fun retry() {
        launch {
            model.retry()
        }
    }

    override fun onJoinClick(communityId: String) {
        launch {
            if(!model.subscribeUnsubscribe(communityId)) {
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }

    fun onBackButtonClick() {
        _command.value = NavigateBackwardCommand()
    }

    private fun loadPage() {
        launch {
            model.loadPage()
        }
    }
}