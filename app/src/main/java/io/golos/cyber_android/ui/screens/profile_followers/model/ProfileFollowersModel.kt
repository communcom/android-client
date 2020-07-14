package io.golos.cyber_android.ui.screens.profile_followers.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.domain.dto.ErrorInfoDomain
import io.golos.domain.dto.UserIdDomain

interface ProfileFollowersModel : ModelBase {
    val pageSize: Int

    val isCurrentUser: Boolean

    fun getItems(filter: FollowersFilter) : LiveData<List<VersionedListItem>>

    suspend fun loadPage(filter: FollowersFilter)

    suspend fun retry(filter: FollowersFilter)

    /**
     * @return true in case of success
     */
    suspend fun subscribeUnsubscribe(userId: UserIdDomain, filter: FollowersFilter): ErrorInfoDomain?

    suspend fun getUserName(): String
}