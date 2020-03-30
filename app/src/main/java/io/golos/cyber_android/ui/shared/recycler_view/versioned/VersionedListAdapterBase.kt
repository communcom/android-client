package io.golos.cyber_android.ui.shared.recycler_view.versioned

import io.golos.cyber_android.ui.shared.recycler_view.DiffAlgBase
import io.golos.cyber_android.ui.shared.recycler_view.ListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase

abstract class VersionedListAdapterBase<TListItemEventsProcessor>(
    listItemEventsProcessor: TListItemEventsProcessor,
    private val pageSize: Int?
) : ListAdapterBase<TListItemEventsProcessor, VersionedListItem>(listItemEventsProcessor) {

    override fun createDiffAlg(
        oldData: List<VersionedListItem>,
        newData: List<VersionedListItem>
    ): DiffAlgBase<VersionedListItem> =
        VersionedDiffAlg(oldData, newData)

    override fun onBindViewHolder(holder: ViewHolderBase<TListItemEventsProcessor, VersionedListItem>, position: Int) {
        super.onBindViewHolder(holder, position)
        checkNextPageReached(pageSize, items.size, position)
    }

    protected open fun checkNextPageReached(pageSize: Int?, itemsSize: Int, position: Int) {
        pageSize?.let {
            if (position > itemsSize - it / 2) {
                onNextPageReached()
            }
        }
    }

    protected open fun onNextPageReached() {}
}