package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view.items

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.common.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view_model.MyFeedListListener
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.VotingWidget
import io.golos.cyber_android.utils.positiveValue
import io.golos.domain.use_cases.post.post_dto.*
import kotlinx.android.synthetic.main.item_feed_content.view.*
import kotlinx.android.synthetic.main.item_post_content.view.*
import kotlinx.android.synthetic.main.item_post_controls.view.*

class PostItem(
    private val post: Post,
    private val listener: MyFeedListListener
) : BaseRecyclerItem() {

    override fun getLayoutId(): Int = R.layout.item_post_content

    private var recycledViewPool: RecyclerView.RecycledViewPool? = null

    private val feedAdapter: RecyclerAdapter = RecyclerAdapter()

    override fun initView(context: Context, view: View) {
        super.initView(context, view)
        view.feedContent.apply {
            adapter = feedAdapter
            layoutManager = LinearLayoutManager(view.context)
        }
    }

    fun setRecycledViewPool(recycledViewPool: RecyclerView.RecycledViewPool) {
        this.recycledViewPool = recycledViewPool
    }

    override fun renderView(context: Context, view: View) {
        super.renderView(context, view)
        setUpFeedContent(view, post.body)
        setPostHeader(view, post)
        setVotesCounter(view, post.votes)
        KiloCounterFormatter().format(post.stats?.commentsCount.positiveValue().toLong())
        setCommentsCounter(view, post.stats?.commentsCount ?: 0)
    }

    private fun setUpFeedContent(view: View, postBlock: PostBlock) {
        view.feedContent.adapter = feedAdapter
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
                    AttachmentPostItem(block, listener)
                }
            }

            is ImageBlock -> ImagePostItem(block, listener)

            is VideoBlock -> VideoPostItem(block, listener)

            is WebsiteBlock -> WebSitePostItem(block, listener)

            is ParagraphBlock -> ParagraphPostItem(block, listener)

            else -> null
        }
    }

    private fun setPostHeader(view: View, post: Post) {
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
        view.postHeader.setHeader(postHeader)
    }

    private fun setVotesCounter(view: View, votes: Post.Votes) {
        val votesCounter = votes.upCount - votes.downCount
        val votingWidget = view.findViewById<VotingWidget>(R.id.votesArea)
        votingWidget.setVoteBalance(votesCounter.positiveValue())
        votingWidget.setDownVoteButtonSelected(votes.hasDownVote)
        votingWidget.setUpVoteButtonSelected(votes.hasUpVote)
    }

    private fun setCommentsCounter(view: View, commentsCounter: Int) {
        view.commentsCountText.text = commentsCounter.toString()
        view.commentsCountText.setOnClickListener {
            listener.onCommentsClicked(post.contentId)
        }
        view.commentsIcon.setOnClickListener {
            listener.onCommentsClicked(post.contentId)
        }
    }
}
