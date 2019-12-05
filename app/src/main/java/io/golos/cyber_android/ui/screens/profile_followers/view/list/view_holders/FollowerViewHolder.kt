package io.golos.cyber_android.ui.screens.profile_followers.view.list.view_holders

class FollowerViewHolder {
}

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

            itemView.communityInfo.text = "$followers ${SpecialChars.bullet} $posts"

            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(community) }

            if(isJoined) {
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