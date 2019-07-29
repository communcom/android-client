package io.golos.cyber_android.ui.shared_fragments.post.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.footer_post_card.view.*
import kotlinx.android.synthetic.main.item_post_header.view.*

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

            val postRating = postModel.votes.upCount - postModel.votes.downCount
            postUpvotesCount.text = "$postRating"
            postVoteStatus.isActivated = postRating >= 0
            postCommentsCount.text = String.format(
                resources.getString(R.string.post_comments_count_format),
                postModel.comments.count
            )
            postViewsCount.text = String.format(
                resources.getString(R.string.post_views_count_format),
                postModel.stats.viewsCount
            )

            bindVoteButtons(postModel, this)
            postUpvote.setOnClickListener { listener.onPostUpvote(postModel) }
            postDownvote.setOnClickListener { listener.onPostDownvote(postModel) }

            postTitle.text = postModel.content.title
            postTitle.visibility =
                if (postModel.content.title.isNotBlank())
                    View.VISIBLE
                else
                    View.GONE
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



