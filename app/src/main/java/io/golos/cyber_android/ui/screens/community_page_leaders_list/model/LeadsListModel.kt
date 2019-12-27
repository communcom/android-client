package io.golos.cyber_android.ui.screens.community_page_leaders_list.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

interface LeadsListModel: ModelBase {
    val items: LiveData<List<VersionedListItem>>

    suspend fun loadLeaders()

    suspend fun retry()
}