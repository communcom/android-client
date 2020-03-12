package io.golos.cyber_android.ui.screens.communities_list.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

interface CommunitiesListModel: ModelBase {
    val pageSize: Int

    val items: LiveData<List<VersionedListItem>>

    suspend fun loadPage()

    fun clear(): Boolean

    suspend fun retry()

    /**
     * @return true in case of success
     */
    suspend fun subscribeUnsubscribe(communityId: String): Boolean
}