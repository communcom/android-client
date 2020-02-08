package io.golos.cyber_android.ui.screens.wallet.model.lists_workers

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

interface ListWorkerSendPoints {
    val items: LiveData<List<VersionedListItem>>

    suspend fun loadPage()

    suspend fun retry()
}