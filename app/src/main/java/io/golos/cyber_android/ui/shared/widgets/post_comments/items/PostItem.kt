package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.Votes
import io.golos.cyber_android.ui.screens.feed_my.view_model.MyFeedListListener
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.screens.post_view.dto.RewardInfo
import io.golos.cyber_android.ui.shared.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.post_view.RecordPostViewManager
import io.golos.cyber_android.ui.shared.widgets.post_comments.donation.DonatePersonsPopup
import io.golos.cyber_android.ui.shared.widgets.post_comments.voting.VotingWidget
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.RewardCurrency
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.*
import io.golos.use_cases.reward.getRewardValue
import io.golos.use_cases.reward.isRewarded
import io.golos.utils.format.KiloCounterFormatter
import io.golos.utils.helpers.positiveValue
import io.golos.utils.id.IdUtil
import kotlinx.android.synthetic.main.item_feed_content.view.*
import kotlinx.android.synthetic.main.item_post_content.view.*
import kotlinx.android.synthetic.main.item_post_controls.view.*
import kotlinx.android.synthetic.main.item_post_controls.view.votesArea
import kotlinx.android.synthetic.main.view_post_voting.view.*

class PostItem(
    val post: Post,
    private val type: Type,
    private val listener: MyFeedListListener,
    private val recordPostViewManager: RecordPostViewManager,
    private val rewardCurrency: RewardCurrency
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
        if (item is PostItem) {
            return post == item.post && rewardCurrency == item.rewardCurrency
        }
        return false
    }

    override fun initView(context: Context, view: View) {
        super.initView(context, view)

        recordPostViewManager.onPostShow(post.contentId)

        view.feedContent.apply {
            adapter = feedAdapter
            layoutManager = LinearLayoutManager(view.context)
            setRecycledViewPool(contentViewPool)
            setItemViewCacheSize(MAX_OFFSCREEN_VIEWS)
        }
    }

    override fun onViewRecycled(view: View) {
        super.onViewRecycled(view)

        recordPostViewManager.onPostHide(post.contentId)

        view.postHeader.release()
        view.votesArea.release()
    }

    fun setRecycledViewPool(recycledViewPool: RecyclerView.RecycledViewPool) {
        this.contentViewPool = recycledViewPool
    }

    fun copy(newRewardCurrency: RewardCurrency): PostItem =
        PostItem(post, type, listener, recordPostViewManager, newRewardCurrency)

    override fun renderView(context: Context, view: View) {
        super.renderView(context, view)
        setUpFeedContent(view, post.body)
        setPostHeader(view, post)
        setVotesCounter(view, post.votes)
        setUpVotesButton(view, post.isMyPost)
        setUpViewCount(view, post.viewCount, true)
        setCommentsCounter(view, post.stats?.commentsCount)
        view.ivShare.setOnClickListener {
            post.shareUrl?.let {
                listener.onShareClicked(it)
            }
        }
        view.setOnClickListener {
            listener.onBodyClicked(post.contentId)
        }

        if(post.donation != null) {
            view.donationPanel.setAmount(post.donation.totalAmount)
            view.donationPanel.visibility = View.VISIBLE
            view.donationPanel.setOnClickListener { DonatePersonsPopup().show(view.donationPanel, post.donation) {
                listener.onDonatePopupClick(post.donation)
            }}
        } else {
            view.donationPanel.visibility = View.INVISIBLE
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

    private fun setUpFeedContent(view: View, postBlock: ContentBlock?) {
        view.feedContent.adapter = feedAdapter

        val contentList : ArrayList<Block> = postBlock?.content as? ArrayList<Block> ?: arrayListOf()
        val newContentList = ArrayList<Block>(contentList)
        ((postBlock?.attachments) as? Block)?.let {
            newContentList.add(it)
        }
        val postContentItems = newContentList
            .filter { createPostBodyItem(newContentList,it) != null }
            .map {
                createPostBodyItem(newContentList,it)!!
            }
        val paragraphContent = arrayListOf<ParagraphItemBlock>()
        postContentItems.map {
            if(it is PostParagraphBlockItem){
                paragraphContent.addAll(it.paragraphBlock.content)
            }
        }
        val newList = postContentItems.filter {
            it !is PostParagraphBlockItem
        }
        (newList as ArrayList<BaseRecyclerItem>).add(0,PostParagraphBlockItem(
            ParagraphBlock(
                IdUtil.generateLongId(),
                paragraphContent
            ),
            listener,
            post.contentId
        ))
        feedAdapter.updateAdapter(newList)
    }

    private fun createPostBodyItem(list:List<Block>,block: Block): BaseRecyclerItem? {
        return when (block) {
            is AttachmentsBlock -> {
                if (block.content.size == 1) {
                    createPostBodyItem(list,block.content.single()) // A single attachment is shown as embed block
                } else {
                    AttachmentBlockItem(block, listener)
                }
            }

            is ImageBlock -> PostImageBlockItem(block, post.contentId, listener)

            is VideoBlock -> VideoBlockItem(block, post.contentId, listener)

            is WebsiteBlock -> WebSiteBlockItem(block, listener)

            is ParagraphBlock -> PostParagraphBlockItem(
                block,
                listener,
                post.contentId
            )

            is RichBlock -> PostRichBlockItem(block, post.contentId, listener)

            is EmbedBlock -> PostEmbedBlockItem(block, post.contentId, listener)

            else -> null
        }
    }

    private fun setPostHeader(view: View, post: Post) {
        val community = post.community
        val author = post.author
        val postHeader = PostHeader(
            community.name,
            community.avatarUrl,
            community.communityId,
            post.meta.creationTime,
            author.username,
            author.userId.userId,
            author.avatarUrl,
            canJoinToCommunity = false,
            isBackFeatureEnabled = false,

            reward = takeIf { post.reward.isRewarded() }?.let {
                RewardInfo(
                    rewardValueInPoints = post.reward.getRewardValue(),
                    rewardValueInCommun = post.reward?.rewardValueCommun,
                    rewardValueInUSD = post.reward?.rewardValueUSD,
                    rewardCurrency = rewardCurrency
                )
            }
        )

        view.postHeader.setHeader(postHeader)
        view.postHeader.setOnUserClickListener {
            listener.onUserClicked(it)
        }
        view.postHeader.setOnCommunityClickListener { communityId ->
            listener.onCommunityClicked(communityId)
        }
        view.postHeader.setOnMenuButtonClickListener {
            listener.onMenuClicked(
                PostMenu(
                    communityId = community.communityId,
                    communityName = community.name,
                    communityAvatarUrl = community.avatarUrl,
                    contentId = ContentIdDomain(
                        communityId = community.communityId,
                        permlink = post.contentId.permlink,
                        userId = author.userId
                    ),
                    creationTime = post.meta.creationTime,
                    authorUsername = author.username,
                    authorUserId = author.userId.userId,
                    authorAvatarUrl = author.avatarUrl,
                    shareUrl = post.shareUrl,
                    isMyPost = post.isMyPost,
                    isSubscribed = post.community.isSubscribed,
                    permlink = post.contentId.permlink,
                    browseUrl = post.meta.browseUrl
                )
            )
        }
        view.postHeader.setOnClickListener{
            listener.onBodyClicked(post.contentId)
        }
        view.postHeader.setOnRewardButtonClickListener { listener.onRewardClick(post.reward) }
    }

    private fun setVotesCounter(view: View, votes: Votes) {
        val votesCounter = votes.upCount - votes.downCount
        val votingWidget = view.findViewById<VotingWidget>(R.id.votesArea)
        votingWidget.setVoteBalance(votesCounter.positiveValue())
        votingWidget.setDownVoteButtonSelected(votes.hasDownVote)
        votingWidget.setUpVoteButtonSelected(votes.hasUpVote)
    }

    private fun setUpVotesButton(view: View, isMyPost: Boolean) {
        if (!isMyPost) {
            view.votesArea.upvoteButton.isEnabled = true
            view.votesArea.downvoteButton.isEnabled = true

            view.votesArea.setOnUpVoteButtonClickListener { listener.onUpVoteClicked(post.contentId) }
            view.votesArea.setOnDownVoteButtonClickListener { listener.onDownVoteClicked(post.contentId) }
            view.votesArea.setOnDonateClickListener {
                listener.onDonateClick(it, post.contentId, post.community.communityId, post.author)
            }
        } else {
            view.votesArea.setUpVoteButtonSelected(true)
            view.votesArea.downvoteButton.isEnabled = false
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

    private companion object{

        private const val MAX_OFFSCREEN_VIEWS = 15
    }
}