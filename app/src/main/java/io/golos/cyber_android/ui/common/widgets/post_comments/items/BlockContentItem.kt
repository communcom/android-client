package io.golos.cyber_android.ui.common.widgets.post_comments.items

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.common.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.common.widgets.post_comments.VotingWidget
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.Votes
import io.golos.cyber_android.ui.screens.my_feed.view_model.MyFeedListListener
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.domain.use_cases.post.post_dto.*
import io.golos.utils.positiveValue
import kotlinx.android.synthetic.main.item_feed_content.view.*
import kotlinx.android.synthetic.main.item_post_content.view.*
import kotlinx.android.synthetic.main.item_post_controls.view.*
import kotlinx.android.synthetic.main.item_post_controls.view.votesArea
import kotlinx.android.synthetic.main.view_post_voting.view.*

class BlockContentItem(
    val post: Post,
    private val type: Type,
    private val listener: MyFeedListListener
) : BaseRecyclerItem() {

    enum class Type {
        FEED,
        PROFILE
    }

    override fun getLayoutId(): Int = R.layout.item_post_content

    private var contentViewPool: RecyclerView.RecycledViewPool? = null

    private val feedAdapter: RecyclerAdapter = RecyclerAdapter()

    override fun areItemsTheSame(): Int = post.contentId.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is BlockContentItem) {
            return post == item.post
        }
        return false
    }

    override fun initView(context: Context, view: View) {
        super.initView(context, view)
        view.feedContent.apply {
            adapter = feedAdapter
            layoutManager = LinearLayoutManager(view.context)
            setRecycledViewPool(contentViewPool)
        }
    }

    fun setRecycledViewPool(recycledViewPool: RecyclerView.RecycledViewPool) {
        this.contentViewPool = recycledViewPool
    }

    override fun renderView(context: Context, view: View) {
        super.renderView(context, view)
        setUpFeedContent(view, post.body)
        setPostHeader(view, post)
        setVotesCounter(view, post.votes)
        setUpVotesButton(view, post.isMyPost)
        setUpViewCount(view, post.stats?.viewCount, type == Type.PROFILE)
        setCommentsCounter(view, post.stats?.commentsCount)
        view.ivShare.setOnClickListener {
            post.shareUrl?.let {
                listener.onShareClicked(it)
            }
        }
    }

    private fun setUpViewCount(view: View, count: Int?, isNeedToShow: Boolean) {
        if (isNeedToShow) {
            view.viewIcon.visibility = View.VISIBLE
            view.viewCountText.visibility = View.VISIBLE
            view.viewCountText.text = KiloCounterFormatter.format(count.positiveValue())
        } else {
            view.viewIcon.visibility = View.INVISIBLE
            view.viewCountText.visibility = View.INVISIBLE
        }
    }

    private fun setUpFeedContent(view: View, postBlock: PostBlock?) {
        view.feedContent.adapter = feedAdapter
        val contentList : ArrayList<Block> = postBlock?.content as? ArrayList<Block> ?: arrayListOf()
        val newContentList = ArrayList<Block>(contentList)
        ((postBlock?.attachments) as? Block)?.let {
            newContentList.add(it)
        }
        val postContentItems = newContentList
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
                    AttachmentBlockItem(block, listener)
                }
            }

            is ImageBlock -> ImageBlockItem(block, post.contentId, listener)

            is VideoBlock -> VideoBlockItem(block, post.contentId, listener)

            is WebsiteBlock -> WebSiteBlockItem(block, listener)

            is ParagraphBlock -> ParagraphBlockItem(
                block,
                listener,
                post.contentId
            )

            is RichBlock -> RichBlockItem(block, post.contentId, listener)

            is EmbedBlock -> EmbedBlockItem(block, post.contentId, listener)

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
            isBackFeatureEnabled = false,
            isJoinFeatureEnabled = false
        )
        view.postHeader.setHeader(postHeader)
        view.postHeader.setOnUserClickListener {
            listener.onUserClicked(it)
        }
        view.postHeader.setOnMenuButtonClickListener {
            listener.onMenuClicked(
                PostMenu(
                    communityId = community.communityId,
                    communityName = community.name,
                    communityAvatarUrl = community.avatarUrl,
                    contentId = ContentId(
                        communityId = community.communityId,
                        permlink = post.contentId.permlink,
                        userId = author.userId
                    ),
                    creationTime = post.meta.creationTime,
                    authorUsername = author.username,
                    authorUserId = author.userId,
                    shareUrl = post.shareUrl,
                    isMyPost = post.isMyPost,
                    isSubscribed = post.community.isSubscribed,
                    permlink = post.contentId.permlink
                )
            )
        }
    }

    override fun onViewRecycled(view: View) {
        super.onViewRecycled(view)
        view.postHeader.release()
        view.votesArea.release()
    }

    private fun setVotesCounter(view: View, votes: Votes) {
        val votesCounter = votes.upCount - votes.downCount
        val votingWidget = view.findViewById<VotingWidget>(R.id.votesArea)
        votingWidget.setVoteBalance(votesCounter.positiveValue())
        votingWidget.setDownVoteButtonSelected(votes.hasDownVote)
        votingWidget.setUpVoteButtonSelected(votes.hasUpVote)
    }

    private fun setUpVotesButton(view: View, isMyPost: Boolean) {
        if (isMyPost) {
            view.votesArea.upvoteButton.visibility = View.VISIBLE
            view.votesArea.downvoteButton.visibility = View.VISIBLE
            view.votesArea.setOnUpVoteButtonClickListener {
                listener.onUpVoteClicked(post.contentId)
            }
            view.votesArea.setOnDownVoteButtonClickListener {
                listener.onDownVoteClicked(post.contentId)
            }
        } else {
            view.votesArea.upvoteButton.visibility = View.INVISIBLE
            view.votesArea.downvoteButton.visibility = View.INVISIBLE
        }
    }

    private fun setCommentsCounter(view: View, commentsCounter: Int?) {
        view.commentsCountText.text = KiloCounterFormatter.format(commentsCounter.positiveValue())
        view.commentsCountText.setOnClickListener {
            listener.onCommentsClicked(post.contentId)
        }
        view.commentsIcon.setOnClickListener {
            listener.onCommentsClicked(post.contentId)
        }
    }
}
