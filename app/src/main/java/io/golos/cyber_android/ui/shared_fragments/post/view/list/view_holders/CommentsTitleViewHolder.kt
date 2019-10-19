package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.SortingType
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.CommentsTitleListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
import io.golos.domain.AppResourcesProvider
import kotlinx.android.synthetic.main.items_post_comments_title.view.*
import javax.inject.Inject

class CommentsTitleViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelItemsClickProcessor, CommentsTitleListItem>(
    parentView,
    R.layout.items_post_comments_title
) {
    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    init {
        App.injections.get<PostPageFragmentComponent>().inject(this)
    }

    override fun init(listItem: CommentsTitleListItem, listItemEventsProcessor: PostPageViewModelItemsClickProcessor) {
        itemView.commentsSortMenuText.text =  when(listItem.soring) {
            SortingType.BY_TIME -> appResourcesProvider.getString(R.string.post_comments_by_time_sort)
            SortingType.INTERESTING_FIRST -> appResourcesProvider.getString(R.string.post_comments_interesting_first_sort)
        }

        itemView.commentsSortMenuText.setOnClickListener { listItemEventsProcessor.onCommentsTitleMenuClick() }
    }

    override fun release() {
        itemView.commentsSortMenuText.setOnClickListener(null)
    }
}