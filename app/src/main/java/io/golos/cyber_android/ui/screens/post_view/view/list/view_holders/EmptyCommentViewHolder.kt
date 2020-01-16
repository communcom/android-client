package io.golos.cyber_android.ui.screens.post_view.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.EmptyCommentsListItem
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import kotlinx.android.synthetic.main.item_post_page_comment_empty.view.*

class EmptyCommentViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, EmptyCommentsListItem>(
    parentView,
    R.layout.item_post_page_comment_empty
) {

    override fun init(
        listItem: EmptyCommentsListItem,
        listItemEventsProcessor: PostPageViewModelListEventsProcessor
    ) {
        itemView.commentEmptyHolder.setTitle(R.string.no_comments_title)
        itemView.commentEmptyHolder.setExplanation(R.string.no_communities_explanation)
    }
}