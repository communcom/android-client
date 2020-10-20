package io.golos.cyber_android.ui.screens.profile_communities.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.screens.profile_communities.dto.CommunitiesCount
import io.golos.cyber_android.ui.screens.profile_communities.model.ProfileCommunitiesModel
import io.golos.cyber_android.ui.screens.profile_communities.view.list.CommunityListItemEventsProcessor
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunitiesListPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToCommunityPageCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileCommunitiesViewModel
@Inject
constructor(
    private val userId: UserIdDomain,
    dispatchersProvider: DispatchersProvider,
    model: ProfileCommunitiesModel,
    sourceData: ProfileCommunities
) : ViewModelBase<ProfileCommunitiesModel>(dispatchersProvider, model),
    CommunityListItemEventsProcessor {

    private val _communitiesCount = MutableLiveData<CommunitiesCount>(CommunitiesCount(sourceData.communitiesSubscribedCount, sourceData.highlightCommunities.size))
    val communitiesCount: LiveData<CommunitiesCount> get() = _communitiesCount

    val items: LiveData<List<VersionedListItem>> get() = model.items

    fun onViewCreated() = model.loadPage()

    fun onSeeAllClick() {
        _command.value = NavigateToCommunitiesListPageCommand(userId)
    }

    override fun onItemClick(communityId: CommunityIdDomain) {
        _command.value = NavigateToCommunityPageCommand(communityId)
    }

    override fun onFolllowUnfollowClick(communityId: CommunityIdDomain) {
        launch {
            model.subscribeUnsubscribe(communityId)?.let {
                _command.value = ShowMessageTextCommand(it.message)
            }
        }
    }
}
