package io.golos.cyber_android.ui.screens.ftue_search_community.view.item.community

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueItemListModelEventProcessor
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community.FtueCommunityListItem
import kotlinx.android.synthetic.main.view_profile_communities_community_list_item.view.*

class FtueCommunityItem(
    parentView: ViewGroup
) : ViewHolderBase<FtueItemListModelEventProcessor, FtueCommunityListItem>(
    parentView,
    R.layout.view_profile_communities_community_list_item
) {

    override fun init(listItem: FtueCommunityListItem, listItemEventsProcessor: FtueItemListModelEventProcessor) {
        itemView.nameLabel.text = listItem.community.name
        itemView.membersLabel.text = getMembersCount(listItem.community.subscribersCount, itemView.context.resources)

        if (listItem.community.isSubscribed) {
            itemView.followingButton.visibility = View.VISIBLE
            itemView.followButton.visibility = View.GONE
            itemView.followingButton.setOnClickListener {
                listItemEventsProcessor.onUnFollowFromCommunity(listItem.community)
            }
        } else {
            itemView.followingButton.visibility = View.GONE
            itemView.followButton.visibility = View.VISIBLE
            itemView.followButton.setOnClickListener {
                listItemEventsProcessor.onFollowToCommunity(listItem.community)
            }
        }

        Glide.with(itemView.context)
            .load(listItem.community.coverUrl.checkImageUrl())
            .transform(CircleCrop())
            .into(itemView.coverImage)

        Glide.with(itemView.context)
            .load(listItem.community.avatarUrl.checkImageUrl())
            .transform(CircleCrop())
            .override(100, 100)
            .into(itemView.avatarImage)
    }

    override fun release() {
        itemView.followingButton.setOnClickListener(null)
        itemView.followButton.setOnClickListener(null)
    }

    private fun String?.checkImageUrl() = if (isNullOrEmpty()) "file:///android_asset/bcg_blue.webp" else this

    private fun getMembersCount(count: Int, resources: Resources): String {
        val formCount = KiloCounterFormatter.format(count)
        val quantityString = resources.getQuantityString(R.plurals.plural_members, count)
        return "$formCount $quantityString"
    }
}