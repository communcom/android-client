package io.golos.cyber_android.ui.screens.post_view.view.list.view_holders.comments

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.post_view.di.PostPageFragmentComponent
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.SecondLevelCommentListItem
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.widgets.post_comments.DonationPanelWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.voting.VotingWidget
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import kotlinx.android.synthetic.main.item_comment.view.*

class SecondLevelCommentViewHolder(
    parentView: ViewGroup,
    commentsViewPool: RecyclerView.RecycledViewPool
) : CommentViewHolderBase<SecondLevelCommentListItem>(
    parentView,
    commentsViewPool
) {
    override val _userAvatar: ImageView
        get() = itemView.ivAvatar

    override val _voting: VotingWidget
        get() = itemView.voting



    override val _content: RecyclerView
        get() = itemView.rvCommentContent

    override val _replyAndTimeText: TextView
        get() = itemView.replyAndTimeText
    override val _donateText: TextView
        get() = itemView.donateText
    override val _processingProgress: ProgressBar
        get() = itemView.processingProgressBar

    override val _warning: ImageView
        get() = itemView.warningIcon

    override val _rootView: View
        get() = itemView

    override val _commentUserName: TextView
        get() = itemView.commentUserName

    override val _donateCoin: ImageView
        get() = itemView.vIconCoin

    override fun inject() = App.injections.getBase<PostPageFragmentComponent>().inject(this)

    override fun init(listItem: SecondLevelCommentListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        // Add second-level margin
        val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.marginStart = itemView.context.resources.getDimension(R.dimen.post_comments_second_level_margin).toInt()
        itemView.layoutParams = layoutParams

        super.init(listItem, listItemEventsProcessor)
    }

    override fun getParentAuthor(listItem: SecondLevelCommentListItem): UserBriefDomain? =
        if(listItem.repliedCommentLevel == 0) null else listItem.repliedAuthor
}