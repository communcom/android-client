package io.golos.cyber_android.ui.screens.post_view.view.list.view_holders

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.SecondLevelCommentLoadingListItem
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase

class SecondLevelCommentLoadingViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, SecondLevelCommentLoadingListItem>(
    parentView,
    R.layout.item_post_comments_loading
) {
    override fun init(listItem: SecondLevelCommentLoadingListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        // Add second-level margin
        val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.marginStart = itemView.resources.getDimension(R.dimen.post_comments_second_level_margin).toInt()
        itemView.layoutParams = layoutParams
    }
}