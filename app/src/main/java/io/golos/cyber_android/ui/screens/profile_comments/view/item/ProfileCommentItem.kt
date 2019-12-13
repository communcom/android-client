package io.golos.cyber_android.ui.screens.profile_comments.view.item

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.common.glide.loadAvatar
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.profile_comments.model.ProfileCommentsModelEventProcessor
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import io.golos.domain.use_cases.post.post_dto.Block
import io.golos.utils.positiveValue
import kotlinx.android.synthetic.main.item_post_comment.view.*

class ProfileCommentItem(
    parentView: ViewGroup
) : ViewHolderBase<ProfileCommentsModelEventProcessor, ProfileCommentListItem>(
    parentView,
    R.layout.item_post_comment
) {

    private var recycledViewPool: RecyclerView.RecycledViewPool? = null

    private val commentContentAdapter: RecyclerAdapter = RecyclerAdapter()

    fun setRecycledViewPool(recycledViewPool: RecyclerView.RecycledViewPool) {
        this.recycledViewPool = recycledViewPool
    }

    override fun init(
        listItem: ProfileCommentListItem,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor
    ) {
        itemView.userAvatar.loadAvatar(listItem.comments.author.avatarUrl)
        //itemView.mainCommentText.text = listItem.comments.commentText //todo need to create spannable text

        setupVoting(listItem, listItemEventsProcessor)

        itemView.processingProgressBar.visibility = View.INVISIBLE
        itemView.warningIcon.visibility = View.INVISIBLE
        itemView.replyAndTimeText.visibility = View.INVISIBLE
        setupCommentContent(listItem)
    }

    private fun setupCommentContent(listItem: ProfileCommentListItem) {
        with(itemView) {
            rvCommentContent.adapter = commentContentAdapter
            val body = listItem.comments.body
            val contentList: ArrayList<Block> = body?.content as? ArrayList<Block> ?: arrayListOf()
            val newContentList = ArrayList<Block>(contentList)
            ((body?.attachments) as? Block)?.let {
                newContentList.add(it)
            }
            /*val postContentItems = newContentList
                .filter { createPostBodyItem(it) != null }
                .map {
                    createPostBodyItem(it)!!
                }*/
            //commentContentAdapter.updateAdapter(postContentItems)
        }
    }

    /*private fun createPostBodyItem(block: Block): BaseRecyclerItem? {
        return when (block) {
            is AttachmentsBlock -> {
                if (block.content.size == 1) {
                    createPostBodyItem(block.content.single()) // A single attachment is shown as embed block
                } else {
                    AttachmentPostItem(block, listener)
                }
            }

            is ImageBlock -> ImagePostItem(block, post.contentId, listener)

            is VideoBlock -> VideoPostItem(block, post.contentId, listener)

            is WebsiteBlock -> WebSitePostItem(block, listener)

            is ParagraphBlock -> ParagraphPostItem(block, listener, post.contentId)

            is RichBlock -> RichPostItem(block, post.contentId, listener)

            is EmbedBlock -> EmbedPostItem(block, post.contentId, listener)

            else -> null
        }
    }*/

    private fun setupVoting(
        listItem: ProfileCommentListItem,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor
    ) {
        with(itemView) {
            val votes = listItem.comments.votes
            val votesCounter = votes.upCount - votes.downCount
            voting.setVoteBalance(votesCounter.positiveValue())
            voting.setUpVoteButtonSelected(votes.hasUpVote)
            voting.setDownVoteButtonSelected(votes.hasDownVote)

            voting.setOnUpVoteButtonClickListener {
                //TODO kv need add right call
                //listItemEventsProcessor.onCommentUpVoteClick(listItem.comments.contentId)
            }
            voting.setOnDownVoteButtonClickListener {
                //TODO kv need add right call
                //listItemEventsProcessor.onCommentDownVoteClick(listItem.comments.contentId)
            }
        }
    }

    override fun release() {
        super.release()
        itemView.voting.release()
    }
}