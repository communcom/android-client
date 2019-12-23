package io.golos.cyber_android.ui.screens.communities_list.view.list.view_holders

import android.annotation.SuppressLint
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.common.glide.loadCommunity
import io.golos.cyber_android.ui.common.formatters.size.PluralSizeFormatter
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.common.recycler_view.versioned.CommunityListItem
import io.golos.cyber_android.ui.screens.communities_list.view.list.CommunityListItemEventsProcessor
import kotlinx.android.synthetic.main.view_communities_community_list_item.view.*

class CommunityListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<CommunityListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_communities_community_list_item
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

        with(listItem) {
            itemView.communityTitle.text = community.name

            val followers = followersFormatter.format(community.subscribersCount)
            val posts = postsFormatter.format(community.postsCount)

            itemView.communityInfo.text = "$followers ${SpecialChars.BULLET} $posts"

            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(community) }

            if(isInPositiveState) {
                itemView.joinButton.text = itemView.context.resources.getString(R.string.joined_to_community)
            } else {
                itemView.joinButton.text = itemView.context.resources.getString(R.string.join_to_community)
            }

            itemView.joinButton.setOnClickListener { listItemEventsProcessor.onJoinClick(community.communityId) }

            itemView.ivLogo.loadCommunity(community.avatarUrl)
        }
    }

    override fun release() {
        itemView.joinButton.setOnClickListener(null)
        itemView.setOnClickListener(null)
    }
}