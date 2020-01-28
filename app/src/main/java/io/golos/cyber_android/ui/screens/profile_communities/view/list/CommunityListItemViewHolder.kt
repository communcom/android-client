package io.golos.cyber_android.ui.screens.profile_communities.view.list

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.transformations.RoundFrameTransformation
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.profile_communities.dto.CommunityListItem
import io.golos.cyber_android.ui.shared.glide.loadCommunityItemAvatar
import io.golos.cyber_android.ui.shared.glide.loadCommunityItemCover
import kotlinx.android.synthetic.main.view_profile_communities_community_list_item.view.*

class CommunityListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<CommunityListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_profile_communities_community_list_item
) {
    private var avatarGlideTarget: Target<*>? = null
    private var coverGlideTarget: Target<*>? = null

    @SuppressLint("SetTextI18n")
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: CommunityListItemEventsProcessor) {
        if (listItem !is CommunityListItem) {
            return
        }

        with(listItem) {
            itemView.nameLabel.text = community.name

            val valuePart = KiloCounterFormatter.format(community.subscribersCount)
            val clarification = itemView.context.resources.getQuantityString(R.plurals.plural_members, community.subscribersCount)
            itemView.membersLabel.text = "$valuePart $clarification"

            if (isJoined) {
                itemView.followingButton.visibility = View.VISIBLE
                itemView.followButton.visibility = View.GONE

                itemView.followingButton.setOnClickListener { listItemEventsProcessor.onFolllowUnfollowClick(community.communityId) }
            } else {
                itemView.followingButton.visibility = View.GONE
                itemView.followButton.visibility = View.VISIBLE

                itemView.followButton.setOnClickListener { listItemEventsProcessor.onFolllowUnfollowClick(community.communityId) }
            }

            coverGlideTarget = itemView.coverImage.loadCommunityItemCover(community.coverUrl)
            avatarGlideTarget = itemView.avatarImage.loadCommunityItemAvatar(community.avatarUrl)

            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(listItem.community.communityId) }
        }
    }

    override fun release() {
        itemView.followingButton.setOnClickListener(null)
        itemView.followButton.setOnClickListener(null)
        itemView.setOnClickListener(null)

        avatarGlideTarget?.clear(itemView.context.applicationContext)
        coverGlideTarget?.clear(itemView.context.applicationContext)
    }
}