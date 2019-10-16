package io.golos.cyber_android.ui.shared_fragments.post.view.view_holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.shared_fragments.post.view.adapter.PostPageAdapter
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.footer_post_card.view.*
import kotlinx.android.synthetic.main.item_post_controls.view.*

/**
 * [RecyclerView.ViewHolder] for displaying post controls (like upvote, downvote etc)
 */
class PostControlsViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_controls, parent, false)) {

    fun bind(view:View, postModel: PostModel, listener: PostPageAdapter.Listener) {
        with(view) {
            val postRating = postModel.votes.upCount - postModel.votes.downCount

            val countersFormatter = KiloCounterFormatter()

            votesText.text = postRating.toString()
            viewCountsText.text = countersFormatter.format(postModel.stats.viewsCount)
            commentsCountText.text = countersFormatter.format(postModel.comments.count)

            bindVoteButtons(postModel, this)
//            postUpvote.setOnClickListener { listener.onPostUpvote(postModel) }
//            postDownvote.setOnClickListener { listener.onPostDownvote(postModel) }
        }
    }

    private fun bindVoteButtons(postModel: PostModel, view: View) {
//        with(view) {
//            upvoteButton.isActivated = postModel.votes.hasUpVote
//            downvoteButton.isActivated = postModel.votes.hasDownVote

//            postDownvoteProgress.visibility =
//                if (postModel.votes.hasDownVotingProgress || postModel.votes.hasVoteCancelProgress && postModel.votes.hasDownVote)
//                    View.VISIBLE
//                else View.GONE
//
//            postUpvoteProgress.visibility =
//                if (postModel.votes.hasUpVoteProgress || postModel.votes.hasVoteCancelProgress && postModel.votes.hasUpVote)
//                    View.VISIBLE
//                else View.GONE
        }
//    }

}



