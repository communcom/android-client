package io.golos.cyber_android.ui.common.comments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.AbstractDiscussionModelAdapter
import io.golos.cyber_android.utils.DateUtils
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.CommentModel
import kotlinx.android.synthetic.main.item_comment.view.*


/**
 * [RecyclerView.Adapter] for [PostEntity]
 */

abstract class CommentsAdapter(protected var values: List<CommentModel>, private val listener: Listener) :
    AbstractDiscussionModelAdapter<CommentModel>() {

    override fun submit(list: List<CommentModel>) {
        val diff = DiffUtil.calculateDiff(CommentsDiffCallback(values, list))
        values = list
        dispatchUpdates(diff)
    }

    abstract fun dispatchUpdates(diff: DiffUtil.DiffResult)

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = values[position]
        holder as CommentViewHolder
        holder.bind(post, listener)
    }

    inner class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(
            commentModel: CommentModel,
            listener: Listener
        ) {
            with(itemView) {
                //todo commentAvatar
                commentRating.text = (commentModel.votes.upCount - commentModel.votes.downCount).toString()
                commentAuthorName.text = commentModel.author.username
                commentDate.text = DateUtils.createTimeLabel(
                    commentModel.meta.time.time,
                    commentModel.meta.elapsedFormCreation.elapsedMinutes,
                    commentModel.meta.elapsedFormCreation.elapsedHours,
                    commentModel.meta.elapsedFormCreation.elapsedDays,
                    context
                )
                commentContent.text =
                    commentModel.content.body.fulCharSequence.ifBlank { commentModel.content.body.previewCharSequence }
                commentReply.setOnClickListener {
                    listener.onReplyClick(commentModel)
                }

                bindVoteButtons(commentModel, this)
                commentUpvote.setOnClickListener { listener.onCommentUpvote(commentModel) }
                commentDownvote.setOnClickListener { listener.onCommentDownvote(commentModel) }

                commentRoot.setPadding(
                    getPaddingStartForCommentLevel(commentModel.content.commentLevel, context),
                    0,
                    0,
                    0
                )
                commentAvatar.layoutParams.width =
                    getAvatarSizeForCommentLevel(commentModel.content.commentLevel, context)
                commentAvatar.layoutParams.height =
                    getAvatarSizeForCommentLevel(commentModel.content.commentLevel, context)

            }
        }

        private fun bindVoteButtons(commentModel: CommentModel, view: View) {
            with(view) {
                commentUpvote.isActivated = commentModel.votes.hasUpVote
                commentDownvote.isActivated = commentModel.votes.hasDownVote

                commentDownvoteProgress.visibility =
                    if (commentModel.votes.hasDownVotingProgress || commentModel.votes.hasVoteCancelProgress && commentModel.votes.hasDownVote)
                        View.VISIBLE
                    else View.GONE

                commentUpvoteProgress.visibility =
                    if (commentModel.votes.hasUpVoteProgress || commentModel.votes.hasVoteCancelProgress && commentModel.votes.hasUpVote)
                        View.VISIBLE
                    else View.GONE
            }
        }

        private fun getAvatarSizeForCommentLevel(level: Int, context: Context): Int {
            val baseSize = context.resources.getDimensionPixelSize(R.dimen.size_post_card_avatar)
            return (baseSize * Math.pow(0.6, level.toDouble())).toInt()
        }

        private fun getPaddingStartForCommentLevel(level: Int, context: Context): Int {
            return when (level) {
                0 -> 0
                else -> context.resources.getDimensionPixelSize(R.dimen.padding_start_comment_level_1)
            }
        }
    }

    interface Listener {

        fun onReplyClick(comment: CommentModel)

        fun onCommentUpvote(comment: CommentModel)

        fun onCommentDownvote(comment: CommentModel)
    }

}