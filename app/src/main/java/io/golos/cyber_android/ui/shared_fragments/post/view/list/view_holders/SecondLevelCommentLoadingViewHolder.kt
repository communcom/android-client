package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.SecondLevelCommentLoadingListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor

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