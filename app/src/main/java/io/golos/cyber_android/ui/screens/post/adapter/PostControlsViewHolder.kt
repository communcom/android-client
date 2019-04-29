package io.golos.cyber_android.ui.screens.post.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.footer_post_card.view.*
import kotlinx.android.synthetic.main.item_post_header.view.*
import java.math.BigInteger

/**
 * [RecyclerView.ViewHolder] for displaying post controls (like upvote, downvote etc)
 */
class PostControlsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(
        postModel: PostModel,
        listener: PostPageAdapter.Listener
    ) {
        with(view) {
            postMedia.visibility = View.GONE
            postContentPreview.visibility = View.GONE

            postUpvotesCount.text = "${postModel.payout.rShares}"
            postVoteStatus.isActivated = postModel.payout.rShares > BigInteger("0")
            postCommentsCount.text = String.format(
                resources.getString(R.string.post_comments_count_format),
                postModel.comments.count
            )
            //todo replace with real data
            postSharesCount.text = String.format(
                resources.getString(R.string.post_shares_count_format),
                10
            )

            bindVoteButtons(postModel, this)
            postUpvote.setOnClickListener { listener.onPostUpvote(postModel) }
            postDownvote.setOnClickListener { listener.onPostDownvote(postModel) }

            postTitle.text = postModel.content.title
            postTitle.visibility =
                if (postModel.content.body.full.isEmpty() && postModel.content.body.embeds.isEmpty())
                    View.VISIBLE
                else
                    View.GONE
            postContent.text = postModel.content.body.preview
            postContent.visibility =
                if (postModel.content.body.full.isEmpty() && postModel.content.body.embeds.isEmpty())
                    View.VISIBLE
                else
                    View.GONE
        }
    }

    private fun bindVoteButtons(postModel: PostModel, view: View) {
        with(view) {
            postUpvote.isActivated = postModel.votes.hasUpVote
            postDownvote.isActivated = postModel.votes.hasDownVote

            postDownvoteProgress.visibility =
                if (postModel.votes.hasDownVotingProgress || postModel.votes.hasVoteCancelProgress && postModel.votes.hasDownVote)
                    View.VISIBLE
                else View.GONE

            postUpvoteProgress.visibility =
                if (postModel.votes.hasUpVoteProgress || postModel.votes.hasVoteCancelProgress && postModel.votes.hasUpVote)
                    View.VISIBLE
                else View.GONE
        }
    }

}



