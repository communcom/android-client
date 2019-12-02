package io.golos.cyber_android.ui.screens.profile_communities.view.list

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.common.glide.transformations.RoundFrameTransformation
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.profile_communities.dto.CommunityListItem
import kotlinx.android.synthetic.main.view_profile_communities_community_list_item.view.*
import com.bumptech.glide.request.target.Target
import io.golos.cyber_android.ui.common.glide.clear

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
        if(listItem !is CommunityListItem) {
            return
        }

        with(listItem) {
            itemView.nameLabel.text = community.name

            val valuePart = KiloCounterFormatter.format(community.subscribersCount)
            val clarification = itemView.context.resources.getQuantityString(R.plurals.plural_members, community.subscribersCount)
            itemView.membersLabel.text = "$valuePart $clarification"

            if(isJoined) {
                itemView.followingButton.visibility = View.VISIBLE
                itemView.followButton.visibility = View.GONE

                itemView.followingButton.setOnClickListener { listItemEventsProcessor.onFolllowUnfollowClick(community.communityId) }
            } else {
                itemView.followingButton.visibility = View.GONE
                itemView.followButton.visibility = View.VISIBLE

                itemView.followButton.setOnClickListener { listItemEventsProcessor.onFolllowUnfollowClick(community.communityId) }
            }

            coverGlideTarget = Glide
                .with(itemView.context.applicationContext)
                .load(checkImageUrl(community.coverUrl))
                .transform(
                    CenterCrop(),
                    RoundedCorners(itemView.context.resources.getDimension(R.dimen.profile_communities_list_item_bcg_corner).toInt())
                )
                .into(itemView.coverImage)

            avatarGlideTarget = Glide
                .with(itemView.context.applicationContext)
                .load(checkImageUrl(community.avatarUrl))
                .transform(
                    CircleCrop(),
                    RoundFrameTransformation(
                        itemView.context.applicationContext,
                        R.dimen.stroke_thin,
                        R.color.white
                    )
                )
                .override(100, 100)
                .into(itemView.avatarImage)

            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(listItem.community.communityId) }
        }
    }

    private fun checkImageUrl(url: String?) = if(url.isNullOrEmpty()) "file:///android_asset/bcg_blue.webp" else url

    override fun release() {
        itemView.followingButton.setOnClickListener(null)
        itemView.followButton.setOnClickListener(null)
        itemView.setOnClickListener(null)

        avatarGlideTarget?.clear(itemView.context.applicationContext)
        coverGlideTarget?.clear(itemView.context.applicationContext)
    }
}