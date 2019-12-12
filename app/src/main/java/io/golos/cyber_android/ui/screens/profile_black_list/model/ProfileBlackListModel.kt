package io.golos.cyber_android.ui.screens.profile_black_list.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.domain.dto.UserIdDomain

interface ProfileBlackListModel : ModelBase {
    val pageSize: Int

    fun getItems(filter: BlackListFilter) : LiveData<List<VersionedListItem>>

    suspend fun loadPage(filter: BlackListFilter)

    suspend fun retry(filter: BlackListFilter)

    suspend fun switchUserState(userId: UserIdDomain): Boolean

    suspend fun switchCommunityState(communityId: String): Boolean
}