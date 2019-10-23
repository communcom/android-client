package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.FirstLevelCommentLoadingListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor

class FirstLevelCommentLoadingViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, FirstLevelCommentLoadingListItem>(
    parentView,
    R.layout.item_post_comments_loading
) {
    override fun init(listItem: FirstLevelCommentLoadingListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        // do nothing
    }
}
