package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.CommentsLoadingErrorListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
import kotlinx.android.synthetic.main.item_post_comments_reloading.view.*

class CommentsLoadingErrorViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelItemsClickProcessor, CommentsLoadingErrorListItem>(
    parentView,
    R.layout.item_post_comments_reloading
) {
    override fun init(listItem: CommentsLoadingErrorListItem, listItemEventsProcessor: PostPageViewModelItemsClickProcessor) {
        itemView.pageLoadingRetryButton.setOnClickListener {  }
    }

    override fun release() {
        super.release()
        itemView.pageLoadingRetryButton.setOnClickListener(null)
    }
}