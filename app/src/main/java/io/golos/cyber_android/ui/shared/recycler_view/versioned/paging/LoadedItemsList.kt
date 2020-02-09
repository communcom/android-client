package io.golos.cyber_android.ui.shared.recycler_view.versioned.paging

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

interface LoadedItemsList {
    val items: LiveData<List<VersionedListItem>>

    suspend fun loadPage()

    suspend fun retry()
}