package io.golos.cyber_android.ui.screens.ftue_search_community.view.item.community

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueItemListModelEventProcessor
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community.FtueCommunityListItem
import io.golos.cyber_android.ui.shared.extensions.setOneClickListener
import io.golos.cyber_android.ui.shared.glide.loadCommunityItemAvatar
import io.golos.cyber_android.ui.shared.glide.loadCommunityItemCover
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.utils.format.KiloCounterFormatter
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

            itemView.followingButton.setOneClickListener {
                listItemEventsProcessor.onUnFollowFromCommunity(listItem.community)
            }
        } else {
            itemView.followingButton.visibility = View.GONE
            itemView.followButton.visibility = View.VISIBLE

            itemView.followButton.setOneClickListener {
                listItemEventsProcessor.onFollowToCommunity(listItem.community)
            }
        }

        itemView.coverImage.loadCommunityItemCover(listItem.community.coverUrl)
        itemView.avatarImage.loadCommunityItemAvatar(listItem.community.avatarUrl)
    }

    override fun release() {
        itemView.followingButton.setOneClickListener(listener = null)
        itemView.followButton.setOneClickListener(listener = null)
    }

    private fun getMembersCount(count: Int, resources: Resources): String {
        val formCount = KiloCounterFormatter.format(count)
        val quantityString = resources.getQuantityString(R.plurals.plural_members, count)
        return "$formCount $quantityString"
    }
}