package io.golos.cyber_android.ui.common.posts

import android.view.View
import android.view.ViewGroup
import io.golos.domain.entities.PostEntity
import android.view.LayoutInflater
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.utils.DateUtils
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.item_post.view.*
import java.math.BigInteger

/**
 * [PagedListAdapter] for [PostEntity]
 */
open class PostsAdapter(diffCallback: DiffUtil.ItemCallback<PostModel>, private val listener: Listener) :
    PagedListAdapter<PostModel, RecyclerView.ViewHolder>(diffCallback) {

    val zero = BigInteger("0")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = getItem(position)
        if (post != null) {
            holder as PostViewHolder
            holder.bind(post, listener)
        }
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