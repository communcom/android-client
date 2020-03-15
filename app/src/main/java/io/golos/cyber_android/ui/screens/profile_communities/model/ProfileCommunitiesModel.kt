package io.golos.cyber_android.ui.screens.profile_communities.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.CommunityIdDomain

interface ProfileCommunitiesModel : ModelBase {
    val items: LiveData<List<VersionedListItem>>

    fun loadPage()

    /**
     * @return true in case of success
     */
    suspend fun subscribeUnsubscribe(communityId: CommunityIdDomain): Boolean
}