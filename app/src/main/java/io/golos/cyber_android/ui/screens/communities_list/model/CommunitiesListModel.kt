package io.golos.cyber_android.ui.screens.communities_list.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ErrorInfoDomain

interface CommunitiesListModel: ModelBase {
    var pageSize: Int

    val items: LiveData<List<VersionedListItem>>

    suspend fun loadPage()

    fun clear(): Boolean

    suspend fun retry()

    /**
     * @return true in case of success
     */
    suspend fun subscribeUnsubscribe(communityId: CommunityIdDomain): ErrorInfoDomain?

    suspend fun unblockCommunity(communityId: CommunityIdDomain)
}