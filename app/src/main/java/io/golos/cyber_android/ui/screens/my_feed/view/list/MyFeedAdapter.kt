package io.golos.cyber_android.ui.screens.my_feed.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.common.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.common.paginator.PaginalAdapter
import io.golos.cyber_android.ui.common.widgets.EditorWidget
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.screens.my_feed.view.items.*
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.VotingWidget
import io.golos.cyber_android.utils.positiveValue
import io.golos.domain.use_cases.post.post_dto.*
import kotlinx.android.synthetic.main.item_create_post.view.*
import kotlinx.android.synthetic.main.item_post_content.view.*
import kotlinx.android.synthetic.main.item_post_controls.view.*
import timber.log.Timber

open class MyFeedAdapter : PaginalAdapter<Post>() {

    override var items: MutableList<Post> = arrayListOf()

    private var user: User? = null

    override val countItemsFromEndForBeginUploadNewPage: Int = 10

    private val rvViewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PROGRESS_ERROR -> getProgressErrorViewHolder(parent)
            CREATE_POST -> CreatePostViewHolder(parent)
            DATA -> PostViewHolder(parent).setRecycledViewPool(rvViewPool)
            else -> PostViewHolder(parent).setRecycledViewPool(rvViewPool)
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

    private fun getPostListPosition(adapterPosition: Int): Int {
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
        when (holder.itemViewType) {
            DATA -> (holder as PostViewHolder).unbind()
            CREATE_POST -> (holder as CreatePostViewHolder).unbind()
        }
    }

    fun updateMyFeedPosts(posts: List<Post>) {
        Timber.d("paginator: posts size -> ${posts.size}")
        val calculateDiff = DiffUtil.calculateDiff(MyFeedDiffUtil(this.items, posts))
        this.items.clear()
        this.items.addAll(posts)
        calculateDiff.dispatchUpdatesTo(this)
    }

    fun updateUser(user: User) {
        this.user = user
        notifyItemChanged(0)
    }

    inner class PostViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_post_content, parent, false)
    ) {

        private val voitingWidget: VotingWidget = itemView.findViewById(R.id.votesArea)

        private val feedContent: RecyclerView = itemView.findViewById(R.id.feedContent) //todo make like a custom view with own VH

        private val feedAdapter: RecyclerAdapter = RecyclerAdapter()

        init {
            feedContent.layoutManager = LinearLayoutManager(itemView.context) //todo to VH
        }

        fun setRecycledViewPool(recycledViewPool: RecyclerView.RecycledViewPool): PostViewHolder{
            feedContent.apply {
                setRecycledViewPool(recycledViewPool)
            }
            return this
        }

        fun bind(post: Post) {
            setUpFeedContent(post.body)
            setPostHeader(post)
            setVotesCounter(post.votes)
            KiloCounterFormatter().format(post.stats?.commentsCount.positiveValue().toLong())
            setCommentsCounter(post.stats?.commentsCount ?: 0)
        }

        private fun setUpFeedContent(postBlock: PostBlock) {
            feedContent.adapter = feedAdapter
            val postContentItems: List<BaseRecyclerItem> = postBlock.content
                .filter { createPostBodyItem(it) != null }
                .map {
                createPostBodyItem(it)!!
            }
            feedAdapter.updateAdapter(postContentItems)
        }

        private fun createPostBodyItem(block: Block): BaseRecyclerItem? {
            return when (block) {
                is AttachmentsBlock -> {
                    if (block.content.size == 1) {
                        createPostBodyItem(block.content.single()) // A single attachment is shown as embed block
                    } else {
                        AttachmentPostItem(block)
                    }
                }

                is ImageBlock -> ImagePostItem(block)

                is VideoBlock -> VideoPostItem(block)

                is WebsiteBlock -> WebSitePostItem(block)

                is ParagraphBlock -> ParagraphPostItem(block)

                else -> null
            }
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

        private fun setVotesCounter(votes: Post.Votes) {
            val votesCounter = votes.upCount - votes.downCount
            voitingWidget.setVoteBalance(votesCounter.positiveValue())
            voitingWidget.setDownVoteButtonSelected(votes.hasDownVote)
            voitingWidget.setUpVoteButtonSelected(votes.hasUpVote)
        }

        private fun setCommentsCounter(commentsCounter: Int) {
            itemView.commentsCountText.text = commentsCounter.toString()
        }

        fun unbind() {

        }
    }

    class CreatePostViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_editor_widget, parent, false)
    ) {
        fun bind(user: User?) {
            user?.let {
                (itemView.editorWidget as EditorWidget).loadUserAvatar(user.avatarUrl, user.userName)
            }
        }

        fun unbind() {
            (itemView.editorWidget as EditorWidget).findViewById<EditorWidget>(R.id.editorWidget).clearUserAvater()
        }
    }

    private companion object {
        const val CREATE_POST = 2
    }
}