package io.golos.cyber_android.ui.common.recycler_view.versioned

import io.golos.cyber_android.ui.common.recycler_view.DiffAlgBase
import io.golos.cyber_android.ui.common.recycler_view.ListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase

abstract class VersionedListAdapterBase<TListItemEventsProcessor>(
    listItemEventsProcessor: TListItemEventsProcessor,
    private val pageSize: Int?
) : ListAdapterBase<TListItemEventsProcessor, VersionedListItem>(listItemEventsProcessor) {

    override fun createDiffAlg(oldData: List<VersionedListItem>, newData: List<VersionedListItem>): DiffAlgBase<VersionedListItem> =
        VersionedDiffAlg(oldData, newData)

    override fun onBindViewHolder(holder: ViewHolderBase<TListItemEventsProcessor, VersionedListItem>, position: Int) {
        super.onBindViewHolder(holder, position)

        pageSize?.let {
            if(position > items.size - it/2) {
                onNextPageReached()
            }
        }
    }

    protected open fun onNextPageReached() {}
}