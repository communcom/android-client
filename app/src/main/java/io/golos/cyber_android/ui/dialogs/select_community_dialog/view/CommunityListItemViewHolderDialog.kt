package io.golos.cyber_android.ui.dialogs.select_community_dialog.view

import android.annotation.SuppressLint
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.UIComponent
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.common.extensions.loadCommunity
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatter
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.CommunityListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.view.list.CommunityListItemEventsProcessor
import io.golos.domain.dependency_injection.Clarification
import kotlinx.android.synthetic.main.view_communities_community_list_item_dialog.view.*
import javax.inject.Inject
import javax.inject.Named

class CommunityListItemViewHolderDialog(
    parentView: ViewGroup
) : ViewHolderBase<CommunityListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_communities_community_list_item_dialog
) {
    @Inject
    @field:Named(Clarification.FOLLOWERS)
    internal lateinit var followersFormatter: SizeFormatter

    @Inject
    @field:Named(Clarification.POSTS)
    internal lateinit var postsFormatter: SizeFormatter

    init {
        App.injections.get<UIComponent>().inject(this)
    }

    @SuppressLint("SetTextI18n")
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: CommunityListItemEventsProcessor) {
        if(listItem !is CommunityListItem) {
            return
        }

        itemView.title.text = listItem.community.name

        val followers = followersFormatter.format(listItem.community.followersCount)
        val posts = postsFormatter.format(listItem.community.postsCount)

        itemView.followersText.text = "$followers ${SpecialChars.bullet} $posts"

        itemView.setOnClickListener { listItemEventsProcessor.onItemClick(listItem.community) }

        itemView.ivLogo.loadCommunity(listItem.community.logo)
    }

    override fun release() {
        itemView.setOnClickListener(null)
    }
}