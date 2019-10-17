package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.view.list.list_items.PostTitleListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
import kotlinx.android.synthetic.main.items_post_title.view.*

class PostTitleViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelItemsClickProcessor, PostTitleListItem>(
    parentView,
    R.layout.items_post_title
) {
    override fun init(listItem: PostTitleListItem, listItemEventsProcessor: PostPageViewModelItemsClickProcessor) {
        itemView.titleText.text = listItem.title
    }
}