package io.golos.cyber_android.ui.screens.post_view.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.FirstLevelCommentLoadingListItem
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase

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
