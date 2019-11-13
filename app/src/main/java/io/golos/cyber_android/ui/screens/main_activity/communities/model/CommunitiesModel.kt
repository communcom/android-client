package io.golos.cyber_android.ui.screens.main_activity.communities.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

interface CommunitiesModel: ModelBase {
    val pageSize: Int

    val items: LiveData<List<VersionedListItem>>

    suspend fun loadPage()

    suspend fun retry()
}