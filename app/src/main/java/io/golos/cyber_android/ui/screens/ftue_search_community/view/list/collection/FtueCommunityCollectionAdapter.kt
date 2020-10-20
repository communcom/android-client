package io.golos.cyber_android.ui.screens.ftue_search_community.view.list.collection

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueItemListModelEventProcessor
import io.golos.cyber_android.ui.screens.ftue_search_community.view.item.collection.FtueCommunityCollectionItem
import io.golos.cyber_android.ui.shared.recycler_view.DiffAlgBase
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

class FtueCommunityCollectionAdapter(
    processor: FtueItemListModelEventProcessor
) : VersionedListAdapterBase<FtueItemListModelEventProcessor>(processor, null) {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderBase<FtueItemListModelEventProcessor, VersionedListItem> {
        return FtueCommunityCollectionItem(parent) as ViewHolderBase<FtueItemListModelEventProcessor, VersionedListItem>
    }

    override fun createDiffAlg(
        oldData: List<VersionedListItem>,
        newData: List<VersionedListItem>
    ): DiffAlgBase<VersionedListItem> {
        return object: DiffAlgBase<VersionedListItem>(oldData, newData) {

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean{
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }
    }
}