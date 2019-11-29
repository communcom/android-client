package io.golos.cyber_android.ui.screens.profile_communities.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.screens.profile_communities.dto.CommunitiesCount
import io.golos.cyber_android.ui.screens.profile_communities.model.ProfileCommunitiesModel
import io.golos.cyber_android.ui.screens.profile_communities.view.list.CommunityListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class ProfileCommunitiesViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: ProfileCommunitiesModel,
    sourceData: ProfileCommunities
) : ViewModelBase<ProfileCommunitiesModel>(dispatchersProvider, model),
    CommunityListItemEventsProcessor {

    private val _communitiesCount = MutableLiveData<CommunitiesCount>(CommunitiesCount(sourceData.communitiesSubscribedCount, sourceData.highlightCommunities.size))
    val communitiesCount: LiveData<CommunitiesCount> get() = _communitiesCount

    val items: LiveData<List<VersionedListItem>> get() = model.items

    fun onViewCreated() = model.loadPage()

    override fun onItemClick(communityId: String) {
        // do nothing
    }

    override fun onJoinClick(communityId: String) {
        // do nothing
    }
}
