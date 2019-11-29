package io.golos.cyber_android.ui.screens.profile_communities.view.list

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

/*
class CommunityListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<CommunityListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_communities_community_list_item
) {

//    @SuppressLint("SetTextI18n")
//    override fun init(listItem: VersionedListItem, listItemEventsProcessor: CommunityListItemEventsProcessor) {
//        if(listItem !is CommunityListItem) {
//            return
//        }
//
//        with(listItem) {
//            itemView.communityTitle.text = community.name
//
//            val followers = followersFormatter.format(community.subscribersCount)
//            val posts = postsFormatter.format(community.postsCount)
//
//            itemView.communityInfo.text = "$followers ${SpecialChars.bullet} $posts"
//
//            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(community) }
//
//            if(isJoined) {
//                itemView.joinButton.text = itemView.context.resources.getString(R.string.joined_to_community)
//            } else {
//                itemView.joinButton.text = itemView.context.resources.getString(R.string.join_to_community)
//            }
//
//            itemView.joinButton.setOnClickListener { listItemEventsProcessor.onJoinClick(community.communityId) }
//
//            itemView.ivLogo.loadCommunity(community.avatarUrl)
//        }
//    }
//
//    override fun release() {
//        itemView.joinButton.setOnClickListener(null)
//        itemView.setOnClickListener(null)
//    }
}*/
