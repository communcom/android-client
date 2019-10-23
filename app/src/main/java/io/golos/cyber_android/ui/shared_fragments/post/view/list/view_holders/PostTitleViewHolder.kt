package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.PostTitleListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor
import kotlinx.android.synthetic.main.item_post_title.view.*

class PostTitleViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, PostTitleListItem>(
    parentView,
    R.layout.item_post_title
) {
    override fun init(listItem: PostTitleListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        itemView.titleText.text = listItem.title
    }
}