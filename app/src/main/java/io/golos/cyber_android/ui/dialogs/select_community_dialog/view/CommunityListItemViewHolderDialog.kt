package io.golos.cyber_android.ui.dialogs.select_community_dialog.view

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.communities_list.view.list.CommunityListItemEventsProcessor
import io.golos.cyber_android.ui.shared.characters.SpecialChars
import io.golos.cyber_android.ui.shared.glide.loadCommunity
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.CommunityListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.GlobalConstants
import io.golos.utils.format.size.PluralSizeFormatter
import kotlinx.android.synthetic.main.view_communities_community_list_item_dialog.view.*

class CommunityListItemViewHolderDialog(
    parentView: ViewGroup
) : ViewHolderBase<CommunityListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_communities_community_list_item_dialog
) {
    private val followersFormatter = PluralSizeFormatter(
        parentView.context.applicationContext,
        R.plurals.formatter_followers_formatted
    )

    private val postsFormatter = PluralSizeFormatter(
        parentView.context.applicationContext,
        R.plurals.formatter_posts_formatted
    )

    @SuppressLint("SetTextI18n")
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: CommunityListItemEventsProcessor) {
        if(listItem !is CommunityListItem) {
            return
        }

        itemView.leaderName.text = listItem.community.name

        val followers = followersFormatter.format(listItem.community.subscribersCount)
        val posts = postsFormatter.format(listItem.community.postsCount)

        if(listItem.community.communityId.code == GlobalConstants.MY_FEED_COMMUNITY_ID) {
            itemView.leaderPoints.visibility = View.GONE
        } else {
            itemView.leaderPoints.visibility = View.VISIBLE
            itemView.leaderPoints.text = "$followers ${SpecialChars.BULLET} $posts"
        }

        itemView.setOnClickListener { listItemEventsProcessor.onItemClick(listItem.community) }

        itemView.ivLogo.loadCommunity(listItem.community.avatarUrl)

        itemView.separator.visibility = if(listItem.isLastItem) View.INVISIBLE else View.VISIBLE
    }

    override fun release() {
        itemView.setOnClickListener(null)
    }
}