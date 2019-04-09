package io.golos.cyber_android.ui.common.posts

import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.AbstractDiscussionModelAdapter
import io.golos.cyber_android.utils.DateUtils
import io.golos.cyber_android.views.utils.BaseTextWatcher
import io.golos.cyber_android.views.utils.colorizeHashTags
import io.golos.cyber_android.views.utils.colorizeLinks
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.PostModel
import kotlinx.android.synthetic.main.footer_post_card.view.*
import kotlinx.android.synthetic.main.header_post_card.view.*
import kotlinx.android.synthetic.main.item_post.view.*
import java.math.BigInteger


/**
 * [RecyclerView.Adapter] for [PostEntity]
 */

abstract class PostsAdapter(private var values: List<PostModel>, private val listener: Listener) :
    AbstractDiscussionModelAdapter<PostModel>() {

    private val zero = BigInteger("0")

    override fun submit(list: List<PostModel>) {
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

        //fix for scrolling EditText inside RecyclerView
        view.postComment.setOnTouchListener { v, event ->
            if (view.postComment.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@setOnTouchListener true
                    }
                }
            }
            return@setOnTouchListener false
        }

        view.postComment.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                view.postSend.isEnabled = view.postComment.length() > 3
                view.postSend.alpha = if (view.postComment.length() > 3) 1f else 0.3f
                s?.colorizeHashTags()
                s?.colorizeLinks()
            }
        })

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
                postAuthorName.text = postModel.community.name
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
                postContentPreview.text = postModel.content.body.previewCharSequence
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
                postSend.setOnClickListener {
                    listener.onSendClick(postModel, postComment.text ?: "")
                }

                postRoot.setOnClickListener { listener.onPostClick(postModel) }

                postComment.setText("")
                postSend.isEnabled = false
                postSend.alpha = 0.3f

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
     * Click listener of [PostsAdapter] items
     */
    interface Listener {

        /**
         * @param post post was clicked
         */
        fun onPostClick(post: PostModel)

        fun onSendClick(post: PostModel, comment: CharSequence)

        fun onUpvoteClick(post: PostModel)

        fun onDownvoteClick(post: PostModel)
    }

}