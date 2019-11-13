package io.golos.cyber_android.ui.screens.my_feed.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.common.paginator.PaginalAdapter
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.VotingWidget
import io.golos.cyber_android.utils.EMPTY
import io.golos.cyber_android.utils.positiveValue
import kotlinx.android.synthetic.main.item_editor_widget.view.*
import kotlinx.android.synthetic.main.item_post_content.view.*
import kotlinx.android.synthetic.main.item_post_controls.view.*
import timber.log.Timber

open class MyFeedAdapter : PaginalAdapter<Post>() {

    override var items: MutableList<Post> = arrayListOf()

    private var user: User? = null

    override val countItemsFromEndForBeginUploadNewPage: Int = 10

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PROGRESS_ERROR -> getProgressErrorViewHolder(parent)
            CREATE_POST -> CreatePostViewHolder(parent)
            DATA -> PostViewHolder(parent)
            else -> PostViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder.itemViewType) {
            DATA -> {
                val post = items[getPostListPosition(position)]
                (holder as PostViewHolder).bind(post)
            }
            CREATE_POST -> {
                (holder as CreatePostViewHolder).bind(user)
            }
        }
    }

    private fun getPostListPosition(adapterPosition: Int): Int{
        //Minus one position because first item is header
        return adapterPosition - 1
    }

    override fun getItemViewType(position: Int): Int {
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
        Timber.d("paginator: posts size -> ${posts.size}")
        val calculateDiff = DiffUtil.calculateDiff(MyFeedDiffUtil(this.items, posts))
        this.items.clear()
        this.items.addAll(posts)
        calculateDiff.dispatchUpdatesTo(this)
    }

    fun updateUser(user: User){
        this.user = user
        notifyItemChanged(0)
    }

    fun hasUser() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        fun bind(user: User?) {
            user?.let {
                itemView.editorWidget.loadUserAvatar(user.avatarUrl, user.userName)
            }
        }
    }

    private companion object {
        const val CREATE_POST = 2
    }
}