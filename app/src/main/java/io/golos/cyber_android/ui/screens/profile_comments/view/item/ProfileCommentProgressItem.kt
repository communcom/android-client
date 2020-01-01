package io.golos.cyber_android.ui.screens.profile_comments.view.item

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.profile_comments.view_model.ProfileCommentsModelEventProcessor
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentProgressListItem
import kotlinx.android.synthetic.main.item_progress_error.view.*

class ProfileCommentProgressItem(
    parentView: ViewGroup
) : ViewHolderBase<ProfileCommentsModelEventProcessor, ProfileCommentProgressListItem>(
    parentView,
    R.layout.item_progress_error
) {

    override fun init(
        listItem: ProfileCommentProgressListItem,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor
    ) {
        itemView.pbPageLoading.visibility = View.VISIBLE
        itemView.btnPageLoadingRetry.visibility = View.GONE
    }
}