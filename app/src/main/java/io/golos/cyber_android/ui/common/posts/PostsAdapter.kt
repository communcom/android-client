package io.golos.cyber_android.ui.common.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.utils.DateUtils
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.item_post.view.*
import java.math.BigInteger

/**
 * [PagedListAdapter] for [PostEntity]
 */

abstract class PostsAdapter(private var values: List<PostModel>, private val listener: Listener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val zero = BigInteger("0")

    fun submit(list: List<PostModel>) {
        val diff = DiffUtil.calculateDiff(PostsDiffCallback(values, list))
        values = list
        dispatchUpdates(diff)
    }

    /**
     * If adapter has any additional rows (like headers or footers) this method needs to be overriden to correctly dispatch
     * updates to this adapter
     */
    abstract fun dispatchUpdates(diffResult: DiffUtil.DiffResult)

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = values[position]
        holder as PostViewHolder
        holder.bind(post, listener)
    }

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(
            postModel: PostModel,
            listener: Listener
        ) {
            with(itemView) {
                postCommunityName.text = postModel.community.name
                postAuthor.text = String.format(
                    context.resources.getString(R.string.post_time_and_author_format),
                    DateUtils.createTimeLabel(
                        postModel.meta.time.time,
                        postModel.meta.elapsedFormCreation.elapsedMinutes,
                        postModel.meta.elapsedFormCreation.elapsedHours,
                        postModel.meta.elapsedFormCreation.elapsedDays,
                        context
                    ),
                    postModel.author.username
                )
                postContentTitle.text = postModel.content.body.preview
                postUpvotesCount.text = "${postModel.payout.rShares}"
                postVoteStatus.isActivated = postModel.payout.rShares > zero

                postCommentsCount.text = String.format(
                    context.resources.getString(R.string.post_comments_count_format),
                    postModel.comments.count
                )
                //todo replace with real data
                postSharesCount.text = String.format(
                    context.resources.getString(R.string.post_shares_count_format),
                    10
                )

                bindVoteButtons(postModel, this)

                postUpvote.setOnClickListener { listener.onUpvoteClick(postModel) }
                postDownvote.setOnClickListener { listener.onDownvoteClick(postModel) }
                postSendComment.setOnClickListener {
                    listener.onSendClick(
                        postModel,
                        postComment.text.toString(),
                        postUpvote.isActivated,
                        postDownvote.isActivated
                    )
                }
                postRoot.setOnClickListener { listener.onPostClick(postModel) }
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

                if (postModel.votes.hasUpVoteProgress
                    || postModel.votes.hasDownVotingProgress
                    || postModel.votes.hasVoteCancelProgress) {
                    postUpvote.isEnabled = false
                    postDownvote.isEnabled = false
                } else {
                    postUpvote.isEnabled = true
                    postDownvote.isEnabled = true
                }
            }
        }
    }

    /**
     * Click listener of [PostsAdapter] items. Supports one operation -
     * [onSendClick]
     */
    interface Listener {

        /**
         * @param post post which gets new comment
         * @param comment content of the new comment
         * @param upvoted true if post was upvoted, false otherwise
         * @param downvoted true if post was downvoted, false otherwise
         */
        fun onSendClick(post: PostModel, comment: String, upvoted: Boolean, downvoted: Boolean)

        /**
         * @param post post was clicked
         */
        fun onPostClick(post: PostModel)

        fun onUpvoteClick(post: PostModel)

        fun onDownvoteClick(post: PostModel)
    }

}