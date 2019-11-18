package io.golos.cyber_android.ui.screens.main_activity.communities.view.list.view_holders

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.common.extensions.loadCommunity
import io.golos.cyber_android.ui.common.formatters.size.PluralSizeFormatter
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.CommunityListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.view.list.CommunityListItemEventsProcessor
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
            itemView.leaderName.text = community.name

            val followers = followersFormatter.format(community.followersCount)
            val posts = postsFormatter.format(community.postsCount)

            itemView.leaderPoints.text = "$followers ${SpecialChars.bullet} $posts"

            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(community) }

            if(isJoined) {
                itemView.voteButton.text = itemView.context.resources.getString(R.string.joined_to_community)
            } else {
                itemView.voteButton.text = itemView.context.resources.getString(R.string.join_to_community)
            }

            itemView.voteButton.setOnClickListener { listItemEventsProcessor.onJoinClick(community.communityId) }
            itemView.voteButton.isEnabled = !isProgress
            itemView.joiningProgress.visibility = if(isProgress) View.VISIBLE else View.INVISIBLE

            itemView.ivLogo.loadCommunity(community.logo)
        }
    }

    override fun release() {
        itemView.voteButton.setOnClickListener(null)
        itemView.setOnClickListener(null)
    }
}