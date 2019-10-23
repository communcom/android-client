package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments

import android.view.ViewGroup
import android.widget.ImageView
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.FirstLevelCommentListItem
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.VotingWidget
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor
import kotlinx.android.synthetic.main.item_post_comment.view.*

class FirstLevelCommentViewHolder(
    parentView: ViewGroup
) : CommentViewHolderBase<FirstLevelCommentListItem>(
    parentView
) {
    override val _userAvatar: ImageView
        get() = itemView.userAvatar
    override val _voting: VotingWidget
        get() = itemView.voting

    override fun inject() = App.injections.get<PostPageFragmentComponent>().inject(this)

    override fun init(listItem: FirstLevelCommentListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        loadAvatarIcon(listItem.author.avatarUrl)

        itemView.mainCommentText.text = getCommentText(listItem.author, null, listItem.currentUserId, listItem.content, true)
        itemView.replyAndTimeText.text = getReplyAndTimeText(listItem.metadata)

        setVoting(listItem.votes)
    }
}
