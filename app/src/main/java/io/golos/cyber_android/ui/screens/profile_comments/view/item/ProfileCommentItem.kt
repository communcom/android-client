package io.golos.cyber_android.ui.screens.profile_comments.view.item

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.common.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.common.glide.loadAvatar
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.screens.my_feed.view.items.*
import io.golos.cyber_android.ui.screens.profile_comments.model.ProfileCommentsModelEventProcessor
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
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
        itemView.userAvatar.loadAvatar(listItem.comment.author.avatarUrl)
        setupVoting(listItem, listItemEventsProcessor)
        itemView.processingProgressBar.visibility = View.INVISIBLE
        itemView.warningIcon.visibility = View.INVISIBLE
        itemView.replyAndTimeText.visibility = View.INVISIBLE
        setupCommentContent(listItem, listItemEventsProcessor)
    }

    private fun setupCommentContent(listItem: ProfileCommentListItem, listItemEventsProcessor: ProfileCommentsModelEventProcessor) {
        with(itemView) {
            rvCommentContent.apply {
                adapter = commentContentAdapter
                setRecycledViewPool(commentsViewPool)
                layoutManager = LinearLayoutManager(itemView.context)
            }
            val body = listItem.comment.body
            val contentList: ArrayList<Block> = body?.content as? ArrayList<Block> ?: arrayListOf()
            val newContentList = ArrayList<Block>(contentList)
            ((body?.attachments) as? Block)?.let {
                newContentList.add(it)
            }
            val postContentItems = newContentList
                .filter { createPostBodyItem(listItem.comment, it, listItemEventsProcessor) != null }
                .map {
                    createPostBodyItem(listItem.comment, it, listItemEventsProcessor)!!
                }
            commentContentAdapter.updateAdapter(postContentItems)
        }
    }

    private fun createPostBodyItem(comment: Comment, block: Block, listItemEventsProcessor: ProfileCommentsModelEventProcessor): BaseRecyclerItem? {
        return when (block) {
            is AttachmentsBlock -> {
                if (block.content.size == 1) {
                    createPostBodyItem(comment, block.content.single(), listItemEventsProcessor) // A single attachment is shown as embed block
                } else {
                    AttachmentPostItem(block, listItemEventsProcessor)
                }
            }

            is ImageBlock -> ImagePostItem(block, comment.contentId, listItemEventsProcessor)

            is VideoBlock -> VideoPostItem(block, comment.contentId, listItemEventsProcessor)

            is WebsiteBlock -> WebSitePostItem(block, listItemEventsProcessor)

            is ParagraphBlock -> ParagraphPostItem(block, listItemEventsProcessor, comment.contentId)

            is RichBlock -> RichPostItem(block, comment.contentId, listItemEventsProcessor)

            is EmbedBlock -> EmbedPostItem(block, comment.contentId, listItemEventsProcessor)

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
                //TODO kv need add right call
                //listItemEventsProcessor.onCommentUpVoteClick(listItem.comment.contentId)
            }
            voting.setOnDownVoteButtonClickListener {
                //TODO kv need add right call
                //listItemEventsProcessor.onCommentDownVoteClick(listItem.comment.contentId)
            }
        }
    }

    override fun release() {
        super.release()
        itemView.voting.release()
    }
}