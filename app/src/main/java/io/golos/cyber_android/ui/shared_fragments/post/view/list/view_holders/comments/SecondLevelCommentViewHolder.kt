package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments

import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.SecondLevelCommentListItem
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.VotingWidget
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
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

    override fun inject() = App.injections.get<PostPageFragmentComponent>().inject(this)

    override fun init(listItem: SecondLevelCommentListItem, listItemEventsProcessor: PostPageViewModelItemsClickProcessor) {
        // Add second-level margin
        val layoutParams = itemView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.marginStart = appResourcesProvider.getDimens(R.dimen.post_comments_second_level_margin).toInt()
        itemView.layoutParams = layoutParams

        loadAvatarIcon(listItem.author.avatarUrl)

        itemView.mainCommentText.text = getCommentText(listItem.author, listItem.parentAuthor, listItem.currentUserId, listItem.content, true)
        itemView.replyAndTimeText.text = getReplyAndTimeText(listItem.metadata)

        setVoting(listItem.votes)
    }
}
