package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.ErrorInfoDomain
import io.golos.domain.dto.UserIdDomain

interface UserDiscoveryModel : ModelBase{
    val isCurrentUser: Boolean

    fun getItems(filter: FollowersFilter) : LiveData<List<VersionedListItem>>

    suspend fun loadPage(filter: FollowersFilter)

    suspend fun retry(filter: FollowersFilter)

    /**
     * @return true in case of success
     */
    suspend fun subscribeUnsubscribe(userId: UserIdDomain, filter: FollowersFilter): ErrorInfoDomain?

}