package io.golos.cyber_android.ui.screens.profile_communities.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.screens.profile_communities.dto.CommunityListItem
import io.golos.domain.utils.IdUtil
import javax.inject.Inject

class ProfileCommunitiesModelImpl
@Inject
constructor(
    private val sourceData: ProfileCommunities
) : ModelBaseImpl(), ProfileCommunitiesModel {

    private var loadedItems: MutableList<VersionedListItem> = mutableListOf()

    private val _items = MutableLiveData<List<VersionedListItem>>(loadedItems)
    override val items: LiveData<List<VersionedListItem>>
        get() = _items

    override fun loadPage() = updateData {
        loadedItems.addAll(sourceData.highlightCommunities.map { it.mapToListItem() })
    }

    private fun updateData(updateAction: (MutableList<VersionedListItem>) -> Unit) {
        updateAction(loadedItems)
        _items.value = loadedItems
    }

    private fun Community.mapToListItem(): VersionedListItem =
        CommunityListItem(
            id = IdUtil.generateLongId(),
            version = 0,
            community = this,
            isJoined = this.isSubscribed,
            isProgress = false
    )
}