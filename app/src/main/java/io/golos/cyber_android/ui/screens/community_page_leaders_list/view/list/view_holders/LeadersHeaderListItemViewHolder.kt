package io.golos.cyber_android.ui.screens.community_page_leaders_list.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view.list.LeadsListItemEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.item_leaders_list_header.view.*

class LeadersHeaderListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<LeadsListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.item_leaders_list_header
) {

    override fun init(listItem: VersionedListItem, listItemEventsProcessor: LeadsListItemEventsProcessor) {
        itemView.tvHeader.setText(R.string.leaders)
    }
}