package io.golos.cyber_android.ui.screens.post_view.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.post_view.dto.SortingType
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.CommentsTitleListItem
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModelListEventsProcessor
import kotlinx.android.synthetic.main.item_post_comments_title.view.*

class CommentsTitleViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, CommentsTitleListItem>(
    parentView,
    R.layout.item_post_comments_title
) {
    override fun init(listItem: CommentsTitleListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        itemView.commentsSortMenuText.text =  when(listItem.soring) {
            SortingType.BY_TIME -> itemView.context.resources.getString(R.string.post_comments_by_time_sort)
            SortingType.INTERESTING_FIRST -> itemView.context.resources.getString(R.string.post_comments_interesting_first_sort)
        }

        itemView.commentsSortMenuText.setOnClickListener { listItemEventsProcessor.onCommentsTitleMenuClick() }
    }

    override fun release() {
        itemView.commentsSortMenuText.setOnClickListener(null)
    }
}