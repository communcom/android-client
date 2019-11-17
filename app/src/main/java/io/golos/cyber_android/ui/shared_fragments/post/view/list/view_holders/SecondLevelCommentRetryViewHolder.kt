package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.SecondLevelCommentRetryListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor
import kotlinx.android.synthetic.main.item_post_comments_retry.view.*

class SecondLevelCommentRetryViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, SecondLevelCommentRetryListItem>(
    parentView,
    R.layout.item_post_comments_retry
) {
    override fun init(listItem: SecondLevelCommentRetryListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        // Add second-level margin
        val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.marginStart = itemView.context.resources.getDimension(R.dimen.post_comments_second_level_margin).toInt()
        itemView.layoutParams = layoutParams

        itemView.pageLoadingRetryButton.setOnClickListener {
            listItemEventsProcessor.onRetryLoadingSecondLevelCommentButtonClick(listItem.parentCommentId)
        }
    }

    override fun release() {
        super.release()
        itemView.pageLoadingRetryButton.setOnClickListener(null)
    }
}