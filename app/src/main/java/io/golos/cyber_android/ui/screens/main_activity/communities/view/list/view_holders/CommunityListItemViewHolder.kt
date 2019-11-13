package io.golos.cyber_android.ui.screens.main_activity.communities.view.list.view_holders

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.CommunitiesFragmentComponent
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.common.extensions.loadCommunity
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatter
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.CommunityListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.view.list.CommunityListItemEventsProcessor
import io.golos.domain.AppResourcesProvider
import io.golos.domain.dependency_injection.Clarification
import kotlinx.android.synthetic.main.view_communities_community_list_item.view.*
import javax.inject.Inject
import javax.inject.Named

class CommunityListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<CommunityListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_communities_community_list_item
) {
    @Inject
    @field:Named(Clarification.FOLLOWERS)
    internal lateinit var followersFormatter: SizeFormatter

    @Inject
    @field:Named(Clarification.POSTS)
    internal lateinit var postsFormatter: SizeFormatter

    @Inject
    internal lateinit var appResources: AppResourcesProvider

    init {
        App.injections.get<CommunitiesFragmentComponent>().inject(this)
    }

    @SuppressLint("SetTextI18n")
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: CommunityListItemEventsProcessor) {
        if(listItem !is CommunityListItem) {
            return
        }

        with(listItem) {
            itemView.title.text = community.name

            val followers = followersFormatter.format(community.followersCount)
            val posts = postsFormatter.format(community.postsCount)

            itemView.followersText.text = "$followers ${SpecialChars.bullet} $posts"

            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(community) }

            if(isJoinInProgress) {
                itemView.joinButton.text = appResources.getString(R.string.join_to_community)
                itemView.joinButton.isEnabled = false
                itemView.joinButton.setOnClickListener(null)

                itemView.joiningProgress.visibility = View.VISIBLE
            } else {
                if(isJoined) {
                    itemView.joinButton.text = appResources.getString(R.string.joined_to_community)
                    itemView.joinButton.isEnabled = false
                    itemView.joinButton.setOnClickListener(null)
                } else {
                    itemView.joinButton.text = appResources.getString(R.string.join_to_community)
                    itemView.joinButton.isEnabled = true
                    itemView.joinButton.setOnClickListener { listItemEventsProcessor.onJoinClick(community.communityId) }
                }

                itemView.joiningProgress.visibility = View.INVISIBLE
            }

            itemView.ivLogo.loadCommunity(community.logo)
        }
    }

    override fun release() {
        itemView.joinButton.setOnClickListener(null)
        itemView.setOnClickListener(null)
    }
}