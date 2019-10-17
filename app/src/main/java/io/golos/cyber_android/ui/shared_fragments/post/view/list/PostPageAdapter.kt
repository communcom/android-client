package io.golos.cyber_android.ui.shared_fragments.post.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared_fragments.post.view.list.list_items.*
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.PostBodyViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.PostControlsViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.PostTitleViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor

class PostPageAdapter(
    listItemEventsProcessor: PostPageViewModelItemsClickProcessor
) : VersionedListAdapterBase<PostPageViewModelItemsClickProcessor>(listItemEventsProcessor) {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<PostPageViewModelItemsClickProcessor, VersionedListItem> {
        return when(viewType) {
            PostPageViewType.POST_TITLE -> PostTitleViewHolder(parent) as ViewHolderBase<PostPageViewModelItemsClickProcessor, VersionedListItem>
            PostPageViewType.POST_BODY -> PostBodyViewHolder(
                parent
            ) as ViewHolderBase<PostPageViewModelItemsClickProcessor, VersionedListItem>
            PostPageViewType.POST_CONTROLS -> PostControlsViewHolder(parent) as ViewHolderBase<PostPageViewModelItemsClickProcessor, VersionedListItem>

            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
    }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is PostTitleListItem -> PostPageViewType.POST_TITLE
            is PostBodyListItem -> PostPageViewType.POST_BODY
            is PostControlsListItem -> PostPageViewType.POST_CONTROLS
            is CommentsTitleListItem -> PostPageViewType.COMMENTS_TITLE
            is CommentListItem -> PostPageViewType.COMMENT

            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
}