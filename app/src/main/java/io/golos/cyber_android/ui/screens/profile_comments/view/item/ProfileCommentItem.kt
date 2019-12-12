package io.golos.cyber_android.ui.screens.profile_comments.view.item

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.glide.loadAvatar
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.profile_comments.model.ProfileCommentsModelEventProcessor
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import kotlinx.android.synthetic.main.item_post_comment.view.*

class ProfileCommentItem(
    parentView: ViewGroup
) : ViewHolderBase<ProfileCommentsModelEventProcessor, ProfileCommentListItem>(
    parentView,
    R.layout.item_post_comment
) {

    override fun init(
        listItem: ProfileCommentListItem,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor
    ) {
        itemView.userAvatar.loadAvatar(listItem.comments.avatarUrl)
        itemView.mainCommentText.text = listItem.comments.commentText //todo need to create spannable text

        setupVoting(listItem, listItemEventsProcessor)

        itemView.processingProgressBar.visibility = View.INVISIBLE
        itemView.warningIcon.visibility = View.INVISIBLE
        itemView.replyAndTimeText.visibility = View.INVISIBLE
    }

    private fun setupVoting(
        listItem: ProfileCommentListItem,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor
    ) {
        with(itemView) {
            voting.setVoteBalance(listItem.comments.voteCount)
            voting.setUpVoteButtonSelected(listItem.comments.hasUpVote)
            voting.setDownVoteButtonSelected(listItem.comments.hasDownVote)

            voting.setOnUpVoteButtonClickListener {
                listItemEventsProcessor.onCommentUpVoteClick(listItem.comments.commentId)
            }
            voting.setOnDownVoteButtonClickListener {
                listItemEventsProcessor.onCommentDownVoteClick(listItem.comments.commentId)
            }
        }
    }

    override fun release() {
        super.release()
        itemView.voting.release()
    }
}