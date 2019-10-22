package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.SecondLevelCommentLoadingListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
import io.golos.domain.AppResourcesProvider
import javax.inject.Inject

class SecondLevelCommentLoadingViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelItemsClickProcessor, SecondLevelCommentLoadingListItem>(
    parentView,
    R.layout.item_post_comments_loading
) {
    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    init {
        App.injections.get<PostPageFragmentComponent>().inject(this)
    }

    override fun init(listItem: SecondLevelCommentLoadingListItem, listItemEventsProcessor: PostPageViewModelItemsClickProcessor) {
        // Add second-level margin
        val layoutParams = itemView.layoutParams as FrameLayout.LayoutParams
        layoutParams.marginStart = appResourcesProvider.getDimens(R.dimen.post_comments_second_level_margin).toInt()
        itemView.layoutParams = layoutParams
    }
}