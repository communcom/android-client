package io.golos.cyber_android.ui.screens.ftue_search_community.view.item.collection

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueItemListModelEventProcessor
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.collection.FtueCommunityCollectionListItem
import kotlinx.android.synthetic.main.item_community_collection.view.*

class FtueCommunityCollectionItem(
    parentView: ViewGroup
) : ViewHolderBase<FtueItemListModelEventProcessor, FtueCommunityCollectionListItem>(
    parentView,
    R.layout.item_community_collection
) {
    override fun init(
        listItem: FtueCommunityCollectionListItem,
        listItemEventsProcessor: FtueItemListModelEventProcessor
    ) {
        val community = listItem.collection.community

        if (community != null) {

            itemView.ivCommunityCollectionImage.visibility = View.VISIBLE
            itemView.ivCommunityCollectionDelete.visibility = View.VISIBLE
            itemView.flCommunityCollection.isSelected = true

            itemView.ivCommunityCollectionDelete.setOnClickListener {
                listItemEventsProcessor.onDeleteCommunityFromCollection(community)
            }

            Glide.with(itemView.context)
                .load(community.avatarUrl.checkImageUrl())
                .transform(CircleCrop())
                .into(itemView.ivCommunityCollectionImage)

        } else {
            itemView.ivCommunityCollectionImage.visibility = View.GONE
            itemView.ivCommunityCollectionDelete.visibility = View.GONE
            itemView.flCommunityCollection.isSelected = false
        }
    }

    override fun release() {
        itemView.ivCommunityCollectionDelete.setOnClickListener(null)
    }

    private fun String?.checkImageUrl() = if (isNullOrEmpty()) "file:///android_asset/bcg_blue.webp" else this
}