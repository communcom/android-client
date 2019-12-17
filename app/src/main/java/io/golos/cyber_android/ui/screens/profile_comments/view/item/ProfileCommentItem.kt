package io.golos.cyber_android.ui.screens.profile_comments.view.item

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.common.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.glide.loadAvatar
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.widgets.post_comments.items.*
import io.golos.cyber_android.ui.dto.Author
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import io.golos.cyber_android.ui.screens.profile_comments.view_model.ProfileCommentsModelEventProcessor
import io.golos.domain.use_cases.post.post_dto.*
import io.golos.utils.positiveValue
import kotlinx.android.synthetic.main.item_post_comment.view.*

class ProfileCommentItem(
    parentView: ViewGroup,
    private val commentsViewPool: RecyclerView.RecycledViewPool? = null
) : ViewHolderBase<ProfileCommentsModelEventProcessor, ProfileCommentListItem>(
    parentView,
    R.layout.item_post_comment
) {
    private val commentContentAdapter: RecyclerAdapter = RecyclerAdapter()

    override fun init(
        listItem: ProfileCommentListItem,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor
    ) {
        setupUserAvatar(listItem.comment.author, listItemEventsProcessor)
        setupVoting(listItem, listItemEventsProcessor)
        itemView.processingProgressBar.visibility = View.INVISIBLE
        itemView.warningIcon.visibility = View.INVISIBLE
        itemView.replyAndTimeText.visibility = View.INVISIBLE
        setupCommentContent(listItem, listItemEventsProcessor)
        itemView.setOnLongClickListener {
            listItemEventsProcessor.onCommentLongClick(listItem.comment)
            true
        }
    }

    private fun setupUserAvatar(author: Author, listItemEventsProcessor: ProfileCommentsModelEventProcessor){
        val userAvatarView = itemView.userAvatar
        userAvatarView.loadAvatar(author.avatarUrl)
        userAvatarView.setOnClickListener {
            listItemEventsProcessor.onUserClicked(author.userId)
        }
    }

    private fun setupCommentContent(listItem: ProfileCommentListItem, listItemEventsProcessor: ProfileCommentsModelEventProcessor) {
        with(itemView) {
            rvCommentContent.apply {
                adapter = commentContentAdapter
                setRecycledViewPool(commentsViewPool)
                layoutManager = LinearLayoutManager(itemView.context)
            }
            val comment = listItem.comment
            val body = comment.body
            val labelCommentDeleted = itemView.context.getString(R.string.comment_deleted)
            val contentList: ArrayList<Block> = body?.content as? ArrayList<Block> ?: arrayListOf()
            val newContentList = ArrayList<Block>(contentList)
            ((body?.attachments) as? Block)?.let {
                newContentList.add(it)
            }

            if(newContentList.isEmpty() && comment.isDeleted ){
                val emptyBlock = ParagraphBlock(arrayListOf(TextBlock( labelCommentDeleted, null, null))) as Block
                newContentList.add(emptyBlock)
            }

            val contentItems = newContentList
                .filter { createPostBodyItem(listItem.comment, it, listItemEventsProcessor) != null }
                .map {
                    createPostBodyItem(listItem.comment, it, listItemEventsProcessor)!!
                }
            commentContentAdapter.updateAdapter(contentItems)
        }
    }

    private fun createPostBodyItem(comment: Comment, block: Block, listItemEventsProcessor: ProfileCommentsModelEventProcessor): BaseRecyclerItem? {
        return when (block) {
            is AttachmentsBlock -> {
                if (block.content.size == 1) {
                    createPostBodyItem(comment, block.content.single(), listItemEventsProcessor) // A single attachment is shown as embed block
                } else {
                    AttachmentBlockItem(block, listItemEventsProcessor)
                }
            }

            is ImageBlock -> ImageBlockItem(
                block,
                comment.contentId,
                listItemEventsProcessor
            )

            is VideoBlock -> VideoBlockItem(
                block,
                comment.contentId,
                listItemEventsProcessor
            )

            is WebsiteBlock -> WebSiteBlockItem(
                block,
                listItemEventsProcessor
            )

            is ParagraphBlock -> ParagraphBlockItem(
                block,
                listItemEventsProcessor,
                comment.contentId
            )

            is RichBlock -> RichBlockItem(
                block,
                comment.contentId,
                listItemEventsProcessor
            )

            is EmbedBlock -> EmbedBlockItem(
                block,
                comment.contentId,
                listItemEventsProcessor
            )

            else -> null
        }
    }

    private fun setupVoting(
        listItem: ProfileCommentListItem,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor
    ) {
        with(itemView) {
            val votes = listItem.comment.votes
            val votesCounter = votes.upCount - votes.downCount
            voting.setVoteBalance(votesCounter.positiveValue())
            voting.setUpVoteButtonSelected(votes.hasUpVote)
            voting.setDownVoteButtonSelected(votes.hasDownVote)

            voting.setOnUpVoteButtonClickListener {
                listItemEventsProcessor.onCommentUpVoteClick(listItem.comment.contentId)
            }
            voting.setOnDownVoteButtonClickListener {
                listItemEventsProcessor.onCommentDownVoteClick(listItem.comment.contentId)
            }
        }
    }

    override fun release() {
        super.release()
        itemView.voting.release()
    }
}