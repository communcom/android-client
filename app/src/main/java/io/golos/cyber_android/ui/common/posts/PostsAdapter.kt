package io.golos.cyber_android.ui.common.posts

import android.view.View
import android.view.ViewGroup
import io.golos.domain.entities.PostEntity
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.item_post.view.*

/**
 * [PagedListAdapter] for [PostEntity]
 */
class PostsAdapter(val diffCallback: DiffUtil.ItemCallback<PostModel>, private val listener: Listener) :
    PagedListAdapter<PostModel, PostsAdapter.PostViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        if (post != null)
            holder.bind(post, listener)
    }

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(
            postModel: PostModel,
            listener: Listener
        ) {
            //TODO maybe replace with data binding
            with(itemView) {
                postCommunityName.text = postModel.community.name
                //TODO combine with time
                postAuthor.text = "4hr ago by ${postModel.author.username}"
                postContentTitle.text = postModel.content.title
                //todo replace with real data
                postUpvotesCount.text = "234"
                postVoteStatus.isActivated = true

                postCommentsCount.text = String.format(
                    context.resources.getString(R.string.post_comments_count_format),
                    postModel.comments.count
                )
                //todo replace with real data
                postSharesCount.text = String.format(
                    context.resources.getString(R.string.post_shares_count_format),
                    10
                )

                //TODO store buttons states
                postUpvote.setOnClickListener { onVoteButtonClick(postUpvote, postDownvote) }
                postDownvote.setOnClickListener { onVoteButtonClick(postDownvote, postUpvote) }
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

        /**
         * Called when one of the vote buttons is clicked. Switch active status of this button and
         * button of opposite vote, if needed
         */
        private fun onVoteButtonClick(voteButton: ImageButton, oppositeVoteButton: ImageButton) {
            voteButton.isActivated = !voteButton.isActivated
            if (voteButton.isActivated) {
                oppositeVoteButton.isActivated = false
                voteButton.setColorFilter(ContextCompat.getColor(voteButton.context, R.color.white))
                oppositeVoteButton.setColorFilter(ContextCompat.getColor(voteButton.context, R.color.dark_gray))
            } else {
                voteButton.setColorFilter(ContextCompat.getColor(voteButton.context, R.color.dark_gray))
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
    }

}