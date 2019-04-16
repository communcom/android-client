package io.golos.cyber_android.ui.screens.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.comments.CommentsAdapter
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.footer_post_card.view.*
import kotlinx.android.synthetic.main.item_loading.view.*
import kotlinx.android.synthetic.main.item_post_header.view.*
import java.math.BigInteger

private const val POST_TYPE = 0
private const val COMMENT_TYPE = 1
private const val LOADING_TYPE = 2

private const val POST_CARD_POSITION = 0


/**
 * This adapter have only one header - Post Card
 */
private const val HEADERS_COUNT = 1

/**
 * Position of the post header
 */
private const val POST_HEADER_POSITION = 0

class PostPageAdapter(
    commentListener: CommentsAdapter.Listener,
    val listener: Listener
) :
    CommentsAdapter(emptyList(), commentListener) {

    override var isLoading = true
        set(value) {
            field = value
            if (itemCount > 0)
                notifyItemChanged(getLoadingViewHolderPosition())
        }

    var postModel: PostModel? = null
        set(value) {
            field = value
            if (itemCount > POST_HEADER_POSITION)
                notifyItemChanged(POST_HEADER_POSITION)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            POST_TYPE -> PostPageAdapter.PostViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_post_header, parent,
                    false
                ).apply {
                    postComment.visibility = View.GONE
                }
            )
            COMMENT_TYPE -> super.onCreateViewHolder(parent, viewType)
            LOADING_TYPE -> PostPageAdapter.LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loading,
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
            LOADING_TYPE -> {
                holder as LoadingViewHolder
                holder.bind(isLoading)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            POST_CARD_POSITION -> POST_TYPE
            getLoadingViewHolderPosition() -> LOADING_TYPE
            else -> COMMENT_TYPE
        }
    }


    fun scrollToComment(commentModel: CommentModel, recyclerView: RecyclerView) {
        if (values.contains(commentModel))
            (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(values.indexOf(commentModel) + getItemsOffset(), 0)
    }

    /**
     * Return headers count to offset real content elements
     */
    private fun getItemsOffset() = HEADERS_COUNT

    /**
     * Returns [getItemCount] of superclass but also adds [getItemsOffset] (which is amount of the headers in adapter)
     * and 1 (which is loading view ath the bottom)
     */
    override fun getItemCount() = super.getItemCount() + getItemsOffset() + 1

    /**
     * Returns position of loading view holder
     */
    private fun getLoadingViewHolderPosition() = itemCount - 1

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
               // postContent.text = postModel.content.body.fulCharSequence.ifBlank { postModel.content.body.previewCharSequence }
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
            }
        }

    }

    interface Listener {
        fun onPostUpvote(postModel: PostModel)
        fun onPostDownvote(postModel: PostModel)
    }
}
