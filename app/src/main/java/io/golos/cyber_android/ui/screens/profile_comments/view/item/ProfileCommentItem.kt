package io.golos.cyber_android.ui.screens.profile_comments.view.item

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.glide.loadAvatar
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.profile_comments.model.ProfileCommentsModelEventProcessor
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import io.golos.utils.positiveValue
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
        itemView.userAvatar.loadAvatar(listItem.comments.authorDomain.avatarUrl)
        //itemView.mainCommentText.text = listItem.comments.commentText //todo need to create spannable text

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
            val votes = listItem.comments.votes
            val votesCounter = votes.upCount - votes.downCount
            voting.setVoteBalance(votesCounter.positiveValue())
            voting.setUpVoteButtonSelected(votes.hasUpVote)
            voting.setDownVoteButtonSelected(votes.hasDownVote)

            voting.setOnUpVoteButtonClickListener {
                //TODO kv need add right call
                //listItemEventsProcessor.onCommentUpVoteClick(listItem.comments.contentId)
            }
            voting.setOnDownVoteButtonClickListener {
                //TODO kv need add right call
                //listItemEventsProcessor.onCommentDownVoteClick(listItem.comments.contentId)
            }
        }
    }

    override fun release() {
        super.release()
        itemView.voting.release()
    }
}