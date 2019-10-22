package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.CommentsLoadingListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor

class CommentsLoadingViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelItemsClickProcessor, CommentsLoadingListItem>(
    parentView,
    R.layout.item_post_comments_loading
) {
    override fun init(listItem: CommentsLoadingListItem, listItemEventsProcessor: PostPageViewModelItemsClickProcessor) {
        // do nothing
    }
}