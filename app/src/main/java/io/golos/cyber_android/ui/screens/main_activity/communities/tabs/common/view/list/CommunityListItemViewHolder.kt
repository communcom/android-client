package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.CommunitiesFragmentComponent
import io.golos.cyber_android.ui.common.extensions.loadCommunity
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatter
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.dto.CommunityListItem
import io.golos.domain.AppResourcesProvider
import kotlinx.android.synthetic.main.view_communities_community_list_item.view.*
import javax.inject.Inject

class CommunityListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<CommunityListItemEventsProcessor, ListItem>(
    parentView,
    R.layout.view_communities_community_list_item
) {
    @Inject
    internal lateinit var followersFormatter: SizeFormatter

    @Inject
    internal lateinit var appResources: AppResourcesProvider

    init {
        App.injections.get<CommunitiesFragmentComponent>().inject(this)
    }

    override fun init(listItem: ListItem, listItemEventsProcessor: CommunityListItemEventsProcessor) {
        if(listItem !is CommunityListItem) {
            return
        }

        itemView.title.text = listItem.community.name
        itemView.followersText.text = followersFormatter.format(listItem.community.followersCount)

        itemView.setOnClickListener { listItemEventsProcessor.onItemClick(listItem.community) }

        if(listItem.isJoined) {
            itemView.joinButton.text = appResources.getString(R.string.joined_to_community)
            itemView.joinButton.isEnabled = false

            itemView.joinButton.setOnClickListener {

            }
        } else {
            itemView.joinButton.text = appResources.getString(R.string.join_to_community)
            itemView.joinButton.isEnabled = true

            itemView.joinButton.setOnClickListener(null)
        }

        itemView.ivLogo.loadCommunity(listItem.community.logo)
    }

    override fun release() {
        itemView.joinButton.setOnClickListener(null)
        itemView.setOnClickListener(null)
    }
}