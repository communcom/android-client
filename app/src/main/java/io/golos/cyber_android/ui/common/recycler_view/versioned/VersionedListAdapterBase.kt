package io.golos.cyber_android.ui.common.recycler_view.versioned

import io.golos.cyber_android.ui.common.recycler_view.DiffAlgBase
import io.golos.cyber_android.ui.common.recycler_view.ListAdapterBase

abstract class VersionedListAdapterBase<TListItemEventsProcessor>(
    listItemEventsProcessor: TListItemEventsProcessor
) : ListAdapterBase<TListItemEventsProcessor, VersionedListItem>(listItemEventsProcessor) {

    override fun createDiffAlg(oldData: List<VersionedListItem>, newData: List<VersionedListItem>): DiffAlgBase<VersionedListItem> =
        VersionedDiffAlg(oldData, newData)
}