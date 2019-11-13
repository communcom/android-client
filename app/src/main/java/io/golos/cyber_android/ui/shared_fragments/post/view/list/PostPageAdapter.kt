package io.golos.cyber_android.ui.shared_fragments.post.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.*
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.*
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments.FirstLevelCommentViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments.SecondLevelCommentViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.PostBodyViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor

class PostPageAdapter(
    private val listEventsProcessor: PostPageViewModelListEventsProcessor,
    pageSize: Int
) : VersionedListAdapterBase<PostPageViewModelListEventsProcessor>(listEventsProcessor, pageSize) {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem> {
        return when(viewType) {
            PostPageViewType.POST_TITLE ->
                PostTitleViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            PostPageViewType.POST_BODY ->
                PostBodyViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            PostPageViewType.POST_CONTROLS ->
                PostControlsViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            PostPageViewType.COMMENTS_TITLE ->
                CommentsTitleViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            PostPageViewType.FIRST_LEVEL_COMMENT ->
                FirstLevelCommentViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            PostPageViewType.FIRST_LEVEL_COMMENTS_LOADING ->
                FirstLevelCommentLoadingViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            PostPageViewType.FIRST_LEVEL_COMMENTS_RETRY ->
                FirstLevelCommentRetryViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            PostPageViewType.SECOND_LEVEL_COMMENT ->
                SecondLevelCommentViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            PostPageViewType.SECOND_LEVEL_COMMENTS_LOADING ->
                SecondLevelCommentLoadingViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            PostPageViewType.SECOND_LEVEL_COMMENTS_RETRY ->
                SecondLevelCommentRetryViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            PostPageViewType.SECOND_LEVEL_COMMENT_COLLAPSED ->
                SecondLevelCommentCollapsedViewHolder(parent) as ViewHolderBase<PostPageViewModelListEventsProcessor, VersionedListItem>

            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
    }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is PostTitleListItem -> PostPageViewType.POST_TITLE
            is PostBodyListItem -> PostPageViewType.POST_BODY
            is PostControlsListItem -> PostPageViewType.POST_CONTROLS

            is CommentsTitleListItem -> PostPageViewType.COMMENTS_TITLE

            is FirstLevelCommentListItem -> PostPageViewType.FIRST_LEVEL_COMMENT
            is FirstLevelCommentLoadingListItem -> PostPageViewType.FIRST_LEVEL_COMMENTS_LOADING
            is FirstLevelCommentRetryListItem -> PostPageViewType.FIRST_LEVEL_COMMENTS_RETRY

            is SecondLevelCommentListItem -> PostPageViewType.SECOND_LEVEL_COMMENT
            is SecondLevelCommentCollapsedListItem -> PostPageViewType.SECOND_LEVEL_COMMENT_COLLAPSED
            is SecondLevelCommentLoadingListItem -> PostPageViewType.SECOND_LEVEL_COMMENTS_LOADING
            is SecondLevelCommentRetryListItem -> PostPageViewType.SECOND_LEVEL_COMMENTS_RETRY

            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() = listEventsProcessor.onNextCommentsPageReached()
}