package io.golos.cyber_android.ui.screens.post

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.comments.CommentsAdapter
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.footer_post_card.view.*
import kotlinx.android.synthetic.main.item_loading.view.*
import kotlinx.android.synthetic.main.item_post_footer.view.*
import kotlinx.android.synthetic.main.item_post_header.view.*
import java.math.BigInteger

private const val POST_TYPE = 0
private const val COMMENT_TYPE = 1
private const val COMMENT_INPUT_TYPE = 2
private const val LOADING_TYPE = 3

class PostAdapter(
    commentListener: CommentsAdapter.Listener,
    val listener: Listener
) :
    CommentsAdapter(emptyList(), commentListener) {

    override var isLoading = true
        set(value) {
            field = value
            notifyItemChanged(itemCount - 2)
        }

    var postModel: PostModel? = null
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            POST_TYPE -> PostAdapter.PostViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_post_header, parent,
                    false
                )
            )
            COMMENT_TYPE -> super.onCreateViewHolder(parent, viewType)
            LOADING_TYPE -> PostAdapter.LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loading,
                    parent,
                    false
                )
            )
            COMMENT_INPUT_TYPE -> PostAdapter.InputViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_post_footer,
                    parent,
                    false
                )
            )
            else -> throw RuntimeException("Unsupported view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            POST_TYPE -> {
                holder as PostViewHolder
                postModel?.let {
                    holder.bind(it, listener)
                }
            }
            COMMENT_TYPE -> super.onBindViewHolder(holder, position - getItemsOffset())
            COMMENT_INPUT_TYPE -> {
                holder as InputViewHolder
                holder.bind("")
            }
            LOADING_TYPE -> {
                holder as LoadingViewHolder
                holder.bind(isLoading)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> POST_TYPE
            itemCount - 2 -> LOADING_TYPE
            itemCount - 1 -> COMMENT_INPUT_TYPE
            else -> COMMENT_TYPE
        }
    }

    private fun getItemsOffset() = 1

    override fun getItemCount() = super.getItemCount() + 3

    private val adapterCallback = AdapterListUpdateCallback(this)
    private val updateCallback = object : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapterCallback.onChanged(position + getItemsOffset(), count, payload)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapterCallback.onMoved(fromPosition + getItemsOffset(), toPosition + getItemsOffset())
        }

        override fun onInserted(position: Int, count: Int) {
            adapterCallback.onInserted(position + getItemsOffset(), count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapterCallback.onRemoved(position + getItemsOffset(), count)
        }
    }

    override fun dispatchUpdates(diff: DiffUtil.DiffResult) {
        diff.dispatchUpdatesTo(updateCallback)
    }

    /**
     * [RecyclerView.ViewHolder] for indicating loading process
     */
    class LoadingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            isLoading: Boolean
        ) {
            view.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }


    /**
     * [RecyclerView.ViewHolder] for comment input
     */
    class InputViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            text: String
        ) {
            view.postCommentBottom.setText(text)
        }
    }

    /**
     * [RecyclerView.ViewHolder] for displaying post content
     */
    class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            postModel: PostModel,
            listener: Listener
        ) {
            with(view) {
                postMedia.visibility = View.VISIBLE
                postMedia.setImageResource(R.drawable.img_example_media)
                postContentPreview.visibility = View.GONE
                postComment.visibility = View.GONE
                postSendComment


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
                postContent.text = postModel.content.body.full ?: postModel.content.body.preview
                postCommentsTitle.text = String.format(
                    resources.getString(R.string.comments_title_format),
                    postModel.comments.count
                )
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

                Log.i("dfsdfsdf", "up - ${postModel.votes.hasUpVote} | upPr - ${postModel.votes.hasUpVoteProgress} | d - ${postModel.votes.hasDownVote} | dPr - ${postModel.votes.hasDownVotingProgress} | cancel - ${postModel.votes.hasVoteCancelProgress}")
            }
        }

    }

    interface Listener {
        fun onPostUpvote(postModel: PostModel)
        fun onPostDownvote(postModel: PostModel)
    }
}
