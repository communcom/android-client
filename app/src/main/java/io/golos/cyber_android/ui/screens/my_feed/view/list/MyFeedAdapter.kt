package io.golos.cyber_android.ui.screens.my_feed.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.common.paginator.PaginalAdapter
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.VotingWidget
import io.golos.cyber_android.utils.positiveValue
import kotlinx.android.synthetic.main.item_post_content.view.*
import kotlinx.android.synthetic.main.item_post_controls.view.*

class MyFeedAdapter : PaginalAdapter<Post>() {

    override var items: MutableList<Post> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PROGRESS_ERROR -> getProgressErrorViewHolder(parent)
            DATA -> PostViewHolder(parent)
            else -> PostViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder.itemViewType) {
            DATA -> {
                val post = items[position]
                (holder as PostViewHolder).bind(post)
            }
        }
    }

    fun updateMyFeedPosts(posts: List<Post>) {
        val calculateDiff = DiffUtil.calculateDiff(MyFeedDiffUtil(this.items, posts))
        this.items.clear()
        this.items.addAll(posts)
        calculateDiff.dispatchUpdatesTo(this)
    }

    inner class PostViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).
        inflate(R.layout.item_post_content, parent, false)) {

        private val voitingWidget: VotingWidget = itemView.findViewById(R.id.votesArea)

        fun bind(post: Post) {
            setPostHeader(post)
            setVotesCounter(post.votes)
            KiloCounterFormatter().format(post.stats?.commentsCount.positiveValue())
            setCommentsCounter(post.stats?.commentsCount ?: 0)
        }

        private fun setPostHeader(post: Post) {
            val community = post.community
            val author = post.author
            val postHeader = PostHeader(
                community.name,
                community.avatarUrl,
                post.meta.creationTime,
                author.username,
                author.userId,
                canJoinToCommunity = false,
                canEdit = true,
                isBackFeatureEnabled = false,
                isJoinFeatureEnabled = false
            )
            itemView.postHeader.setHeader(postHeader)
        }

        private fun setVotesCounter(votes: Post.Votes){
            val votesCounter = votes.upCount - votes.downCount
            voitingWidget.setVoteBalance(votesCounter.positiveValue())
            voitingWidget.setDownVoteButtonSelected(votes.hasDownVote)
            voitingWidget.setUpVoteButtonSelected(votes.hasUpVote)
        }

        private fun setCommentsCounter(commentsCounter: Int){
            itemView.commentsCountText.text = commentsCounter.toString()
        }

        fun unbind(){

        }
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemViewType == DATA) {
            (holder as PostViewHolder).unbind()
        }
    }
}