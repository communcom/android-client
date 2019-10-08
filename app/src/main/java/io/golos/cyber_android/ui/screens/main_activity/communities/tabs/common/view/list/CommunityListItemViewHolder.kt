package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.CommunitiesFragmentComponent
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
    LayoutInflater.from(parentView.context).inflate(R.layout.view_communities_community_list_item, parentView, false)
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

        itemView.title.text = listItem.name
        itemView.followersText.text = followersFormatter.format(listItem.followersQuantity.toLong())

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

        Glide.with(itemView)
            .load(listItem.logoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(itemView.ivLogo)
    }

    override fun release() {
        itemView.joinButton.setOnClickListener(null)
    }
}