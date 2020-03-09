package io.golos.cyber_android.ui.screens.community_page_members.view.list.view_holders

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page_members.dto.CommunityUserListItem
import io.golos.cyber_android.ui.screens.community_page_members.view.MembersListEventsProcessor
import io.golos.cyber_android.ui.shared.characters.SpecialChars
import io.golos.utils.format.size.PluralSizeFormatter
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_profile_followers_list_item.view.*

class CommunityUserViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<MembersListEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_profile_followers_list_item
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
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: MembersListEventsProcessor) {
        if(listItem !is CommunityUserListItem) {
            return
        }

        with(listItem) {
            itemView.title.text = user.userName

            val followers = followersFormatter.format(user.followersCount!!)
            val posts = postsFormatter.format(user.postsCount!!)

            itemView.info.text = "$followers ${SpecialChars.BULLET} $posts"

            itemView.setOnClickListener { listItemEventsProcessor.onUserClick(listItem.user.userId) }
            itemView.joinButton.setOnClickListener { listItemEventsProcessor.onFollowClick(user.userId) }

            itemView.joinButton.visibility = if(canFollow) View.VISIBLE else View.GONE

            itemView.joinButton.isChecked = isFollowing

            itemView.viewDelimiter.visibility = if(listItem.isLastItem) View.GONE else View.VISIBLE

            itemView.avatar.loadAvatar(user.userAvatar)
        }
    }

    override fun release() {
        itemView.joinButton.setOnClickListener(null)
        itemView.setOnClickListener(null)
    }
}