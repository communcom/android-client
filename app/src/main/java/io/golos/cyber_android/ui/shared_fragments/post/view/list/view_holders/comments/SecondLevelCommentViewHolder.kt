package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.SecondLevelCommentListItem
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.VotingWidget
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor
import io.golos.domain.interactors.model.DiscussionAuthorModel
import kotlinx.android.synthetic.main.item_post_comment.view.*

class SecondLevelCommentViewHolder(
    parentView: ViewGroup
) : CommentViewHolderBase<SecondLevelCommentListItem>(
    parentView
) {
    override val _userAvatar: ImageView
        get() = itemView.userAvatar

    override val _voting: VotingWidget
        get() = itemView.voting

    override val _mainCommentText: TextView
        get() = itemView.mainCommentText

    override val _replyAndTimeText: TextView
        get() = itemView.replyAndTimeText

    override val _processingProgress: ProgressBar
        get() = itemView.processingProgressBar

    override val _warning: ImageView
        get() = itemView.warningIcon

    override val _rootView: View
        get() = itemView

    override fun inject() = App.injections.get<PostPageFragmentComponent>().inject(this)

    override fun init(listItem: SecondLevelCommentListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        // Add second-level margin
        val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.marginStart = appResourcesProvider.getDimens(R.dimen.post_comments_second_level_margin).toInt()
        itemView.layoutParams = layoutParams

        super.init(listItem, listItemEventsProcessor)
    }

    override fun getParentAuthor(listItem: SecondLevelCommentListItem): DiscussionAuthorModel? = listItem.parentAuthor
}
