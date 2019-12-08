package io.golos.cyber_android.ui.screens.ftue_search_community.view.list.collection

import android.view.ViewGroup
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueItemListModelEventProcessor
import io.golos.cyber_android.ui.screens.ftue_search_community.view.item.collection.FtueCommunityCollectionItem

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
}