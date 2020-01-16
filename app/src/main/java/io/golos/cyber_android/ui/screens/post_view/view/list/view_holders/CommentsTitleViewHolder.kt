package io.golos.cyber_android.ui.screens.post_view.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.CommentsTitleListItem
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase

class CommentsTitleViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, CommentsTitleListItem>(
    parentView,
    R.layout.item_post_comments_title
) {
    override fun init(
        listItem: CommentsTitleListItem,
        listItemEventsProcessor: PostPageViewModelListEventsProcessor
    ) {
    }
}