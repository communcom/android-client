package io.golos.cyber_android.ui.screens.community_page_reports.fragments.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.view_model.CommunityCommentReportsViewModel
import io.golos.cyber_android.ui.screens.community_page_reports.view.ReportItemInfo
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.view_model.CommunityPostReportsViewModel
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.shared.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.shared.widgets.post_comments.items.*
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.ReportedPostDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.*
import io.golos.utils.id.IdUtil
import kotlinx.android.synthetic.main.item_feed_content.view.*
import kotlinx.android.synthetic.main.item_post_report.view.*
import kotlinx.android.synthetic.main.item_post_report.view.postHeader

class ReportAdapter<T>(val items: List<ReportedPostDomain>, val viewModel: T) : RecyclerView.Adapter<ReportAdapter<T>.MyViewHolder>() {

    private var enableParagraph: Boolean = false

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindItem(reportedPostDomain: ReportedPostDomain) {
            with(itemView) {
                reportedPostDomain.post.run {
                    val postHeaderData =
                        PostHeader(community.name, community.avatarUrl, community.communityId, meta.creationTime, author.username, author.userId.userId, author.avatarUrl, canJoinToCommunity = false, isBackFeatureEnabled = false, reward = null)
                    itemView.postHeader.setHeader(postHeaderData)
                    itemView.postHeader.hideActionMenu()
                    itemView.postHeader.setOnUserClickListener { }
                    itemView.postHeader.setOnCommunityClickListener { }

                    postHeader.setOnUserClickListener {
                        if (viewModel is CommunityPostReportsViewModel) viewModel.onUserInHeaderClick(author.userId.userId)
                        if (viewModel is CommunityCommentReportsViewModel) viewModel.onUserInHeaderClick(author.userId.userId)
                    }
                    postHeader.setOnCommunityClickListener {
                        if (viewModel is CommunityPostReportsViewModel) viewModel.onCommunityClicked(community.communityId)
                        if (viewModel is CommunityCommentReportsViewModel) viewModel.onCommunityClicked(community.communityId)
                    }
                    post_title.text = title
                    post_title.visibility = if (title.isEmpty()) View.GONE else View.VISIBLE
                    val feedAdapter= RecyclerAdapter()
                    val rvViewPool = RecyclerView.RecycledViewPool()
                    itemView.feedContent.apply {
                        adapter = feedAdapter
                        layoutManager = LinearLayoutManager(context)
                        setRecycledViewPool(rvViewPool)
                        setItemViewCacheSize(15)
                    }
                    setUpFeedContent(feedAdapter,this, body)
                }
                reportedPostDomain.entityReport?.forEach {
                    val reportItem = ReportItemInfo(context)
                    reportItem.init(it)
                    lReportItemsContainer.addView(reportItem)
                }

                vMember.text =
                    " ${reportedPostDomain.reports?.reportsCount.toString()} ${resources.getString(R.string.members_reports)}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_report, parent, false))


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    override fun getItemCount(): Int = items.size


    private fun setUpFeedContent(feedAdapter: RecyclerAdapter, postDomain: PostDomain, postBlock: ContentBlock?) {
        postBlock?.let { block ->
            if (block.content?.size!! > 1) {
                enableParagraph = true
            }
            val contentList: ArrayList<Block> = block?.content as? ArrayList<Block> ?: arrayListOf()
            val newContentList = ArrayList<Block>(contentList)
            ((block?.attachments) as? Block)?.let {
                newContentList.add(it)
            }
            val postContentItems = newContentList.filter { createPostBodyItem(postDomain, newContentList, it) != null }.map {
                createPostBodyItem(postDomain, newContentList, it)!!
            }
            val paragraphContent = arrayListOf<ParagraphItemBlock>()
            postContentItems.map {
                if (it is PostParagraphBlockItem) {
                    paragraphContent.addAll(it.paragraphBlock.content.map {
                        if (it is TextBlock && enableParagraph) {
                            if (!it.content.contains("\n")) {
                                it.content += "\n"
                            }
                        }
                        it
                    })
                }
            }
            val newList = postContentItems.filter {
                it !is PostParagraphBlockItem
            }
            (newList as ArrayList<BaseRecyclerItem>).add(0, PostParagraphBlockItem(ParagraphBlock(IdUtil.generateLongId(), paragraphContent), null, postDomain.contentId))
            feedAdapter.updateAdapter(newList)
        }

    }

    private fun createPostBodyItem(postDomain: PostDomain, list: List<Block>, block: Block): BaseRecyclerItem? {
        return when (block) {
            is AttachmentsBlock -> {
                if (block.content.size == 1) {
                    createPostBodyItem(postDomain, list, block.content.single()) // A single attachment is shown as embed block
                } else {
                    AttachmentBlockItem(block, null)
                }
            }

            is ImageBlock -> PostImageBlockItem(block, postDomain.contentId, null)

            is VideoBlock -> VideoBlockItem(block, postDomain.contentId, null)

            is WebsiteBlock -> WebSiteBlockItem(block, null)

            is ParagraphBlock -> PostParagraphBlockItem(block, null, postDomain.contentId)

            is RichBlock -> PostRichBlockItem(block, postDomain.contentId, null)

            is EmbedBlock -> PostEmbedBlockItem(block, postDomain.contentId, null)

            else -> null
        }
    }
}