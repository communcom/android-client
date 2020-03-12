package io.golos.cyber_android.ui.screens.profile_followers.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.dto.NoDataListItem
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersListItemEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_no_data_stub.view.*

class NoDataViewHolder(
    private val parentView: ViewGroup
) : ViewHolderBase<FollowersListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_no_data_stub
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: FollowersListItemEventsProcessor) {
        itemView.noDataExplanation.text = parentView.context.resources.getText(R.string.no_followers_yet)

        itemView.noDataTitle.text = when((listItem as NoDataListItem).filter) {
            FollowersFilter.MUTUALS -> parentView.context.resources.getText(R.string.no_users)
            FollowersFilter.FOLLOWERS -> parentView.context.resources.getText(R.string.no_followers)
            FollowersFilter.FOLLOWINGS -> parentView.context.resources.getText(R.string.no_followings)
        }
    }
}
