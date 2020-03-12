package io.golos.cyber_android.ui.shared.recycler_view.versioned.paging

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

/**
 * Base class for lists with three type of items: 1) general; 2) loading; 3) retry
 * @param [TS] type of source list items
 * @param TD type of general list item
 */
abstract class LoadedItemsUnPagedListBase<TS, TD: VersionedListItem>(
    private val sourceItems: List<TS>
) : LoadedItemsPagedListBase<TD>(Int.MAX_VALUE),
    LoadedItemsList {

    override suspend fun getData(offset: Int): List<TD> = sourceItems.map { mapItem(it) }

    protected abstract fun mapItem(source: TS): TD
}