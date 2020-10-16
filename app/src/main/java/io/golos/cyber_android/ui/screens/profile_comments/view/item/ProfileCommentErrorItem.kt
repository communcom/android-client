package io.golos.cyber_android.ui.screens.profile_comments.view.item

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentErrorListItem
import io.golos.cyber_android.ui.screens.profile_comments.view_model.ProfileCommentsModelEventProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import kotlinx.android.synthetic.main.item_progress_error.view.*

class ProfileCommentErrorItem(
    parentView: ViewGroup
) : ViewHolderBase<ProfileCommentsModelEventProcessor, ProfileCommentErrorListItem>(
    parentView,
    R.layout.item_progress_error
) {

    override fun init(
        listItem: ProfileCommentErrorListItem,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor
    ) {
        itemView.pbPageLoading.visibility = View.GONE
        itemView.btnPageLoadingRetry.visibility = View.VISIBLE
        itemView.btnPageLoadingRetry.setOnClickListener {
            listItemEventsProcessor.onRetryLoadComments()
        }
    }
}