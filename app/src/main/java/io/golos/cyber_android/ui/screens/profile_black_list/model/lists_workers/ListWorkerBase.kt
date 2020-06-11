package io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.ErrorInfoDomain

interface ListWorkerBase<T> {
    val items: LiveData<List<VersionedListItem>>

    suspend fun loadPage()

    suspend fun retry()

    suspend fun switchState(id: T): ErrorInfoDomain?
}