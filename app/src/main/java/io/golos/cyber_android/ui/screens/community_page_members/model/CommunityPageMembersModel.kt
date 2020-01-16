package io.golos.cyber_android.ui.screens.community_page_members.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.UserIdDomain

interface CommunityPageMembersModel : ModelBase {
    val title: String

    val noDataStubText: Int

    val noDataStubExplanation: Int

    val pageSize: Int

    fun getItems() : LiveData<List<VersionedListItem>>

    suspend fun loadPage()

    suspend fun retry()

    /**
     * @return true in case of success
     */
    suspend fun subscribeUnsubscribe(userId: UserIdDomain): Boolean
}