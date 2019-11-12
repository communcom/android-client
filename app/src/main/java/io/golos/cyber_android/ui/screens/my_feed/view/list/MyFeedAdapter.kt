package io.golos.cyber_android.ui.screens.my_feed.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.common.paginator.PaginalAdapter
import io.golos.cyber_android.ui.common.widgets.EditorWidget
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.VotingWidget
import io.golos.cyber_android.utils.EMPTY
import io.golos.cyber_android.utils.positiveValue
import kotlinx.android.synthetic.main.item_editor_widget.view.*
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
                val post = items[getPostViewHolderPosition(position)]
                (holder as PostViewHolder).bind(post)
            }
            CREATE_POST -> {
                if(items.isNotEmpty()){
                    //TODO kv 12/11/2019 нужно переделать на биндинг данныз о пользователе
                    //(holder as CreatePostViewHolder).bind(items[0])
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        //TODO kv 12/11/2019 нужно переделать на биндинг данныз о пользователе добавить по моделе в списке а не по позиции
        return if (position == 0) {
            CREATE_POST
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemViewType == DATA) {
            (holder as PostViewHolder).unbind()
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
            KiloCounterFormatter().format(post.stats?.commentsCount.positiveValue().toLong())
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

    class CreatePostViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).
        inflate(R.layout.item_editor_widget, parent, false)) {
        fun bind(post: Post) {
            val author = post.author
            itemView.editorWidget.loadUserAvatar(author.avatarUrl ?: "", author.username ?: EMPTY)
        }
    }

    private fun getPostViewHolderPosition(adapterPosition: Int): Int{
        return adapterPosition - 1
    }

    protected companion object {
        const val CREATE_POST = 2
    }
}