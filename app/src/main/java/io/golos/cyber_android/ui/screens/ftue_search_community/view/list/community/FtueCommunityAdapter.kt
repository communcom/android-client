package io.golos.cyber_android.ui.screens.ftue_search_community.view.list.community

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import io.golos.cyber_android.ui.common.recycler_view.DiffAlgBase
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueItemListModelEventProcessor
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community.FtueCommunityListItem
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community.FtueCommunityProgressListItem
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community.FtueCommunityRetryListItem
import io.golos.cyber_android.ui.screens.ftue_search_community.view.item.community.FtueCommunityItem
import io.golos.cyber_android.ui.screens.ftue_search_community.view.item.community.FtueCommunityProgressItem
import io.golos.cyber_android.ui.screens.ftue_search_community.view.item.community.FtueCommunityRetryItem
import io.golos.cyber_android.ui.screens.ftue_search_community.view.list.community.FtueCommunityViewType.FTUE_COMMUNITY_ITEM
import io.golos.cyber_android.ui.screens.ftue_search_community.view.list.community.FtueCommunityViewType.FTUE_COMMUNITY_PROGRESS
import io.golos.cyber_android.ui.screens.ftue_search_community.view.list.community.FtueCommunityViewType.FTUE_COMMUNITY_RETRY

class FtueCommunityAdapter(
    processor: FtueItemListModelEventProcessor
) : VersionedListAdapterBase<FtueItemListModelEventProcessor>(processor, null) {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderBase<FtueItemListModelEventProcessor, VersionedListItem> {
        return when (viewType) {
            FTUE_COMMUNITY_ITEM -> {
                FtueCommunityItem(parent) as ViewHolderBase<FtueItemListModelEventProcessor, VersionedListItem>
            }
            FTUE_COMMUNITY_PROGRESS -> {
                FtueCommunityProgressItem(parent) as ViewHolderBase<FtueItemListModelEventProcessor, VersionedListItem>
            }
            FTUE_COMMUNITY_RETRY -> {
                FtueCommunityRetryItem(parent) as ViewHolderBase<FtueItemListModelEventProcessor, VersionedListItem>
            }
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
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

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is FtueCommunityListItem -> FTUE_COMMUNITY_ITEM
        is FtueCommunityProgressListItem -> FTUE_COMMUNITY_PROGRESS
        is FtueCommunityRetryListItem -> FTUE_COMMUNITY_RETRY
        else -> throw UnsupportedOperationException("This type of item is not supported")
    }

    fun isElementNotRetryOrError(position: Int): Boolean {
        val item = items[position]
        return item is FtueCommunityRetryListItem || item is FtueCommunityProgressListItem
    }

    fun addProgress() {
        val item = items.find { it is FtueCommunityProgressListItem }
        if (item == null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.add(FtueCommunityProgressListItem())
            update(adapterItemsList)
        }
    }

    fun removeProgress() {
        val item = items.find { it is FtueCommunityProgressListItem }
        if (item != null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.remove(item)
            update(adapterItemsList)
        }
    }

    fun addRetry() {
        val item = items.find { it is FtueCommunityRetryListItem }
        if (item == null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.add(FtueCommunityRetryListItem())
            update(adapterItemsList)
        }
    }

    fun removeRetry() {
        val item = items.find { it is FtueCommunityRetryListItem }
        if (item != null) {
            val adapterItemsList: ArrayList<VersionedListItem> = ArrayList(items)
            adapterItemsList.remove(item)
            update(adapterItemsList)
        }
    }
}