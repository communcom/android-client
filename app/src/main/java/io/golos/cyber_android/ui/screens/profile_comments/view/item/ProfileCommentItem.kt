package io.golos.cyber_android.ui.screens.profile_comments.view.item

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.Author
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.dto.Meta
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import io.golos.cyber_android.ui.screens.profile_comments.view_model.ProfileCommentsModelEventProcessor
import io.golos.cyber_android.ui.shared.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerAdapter
import io.golos.utils.format.TimeEstimationFormatter
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.widgets.post_comments.items.*
import io.golos.domain.use_cases.post.TextStyle
import io.golos.utils.helpers.appendText
import io.golos.utils.helpers.setSpan
import io.golos.domain.use_cases.post.post_dto.*
import io.golos.utils.id.IdUtil
import io.golos.utils.helpers.SPACE
import io.golos.utils.helpers.positiveValue
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.view_post_voting.view.*

class ProfileCommentItem(
    parentView: ViewGroup,
    private val commentsViewPool: RecyclerView.RecycledViewPool? = null
) : ViewHolderBase<ProfileCommentsModelEventProcessor, ProfileCommentListItem>(
    parentView,
    R.layout.item_comment
) {
    private val commentContentAdapter: RecyclerAdapter = RecyclerAdapter()

    override fun init(
        listItem: ProfileCommentListItem,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor
    ) {
        val comment = listItem.comment
        setupUserAvatar(comment.author, listItemEventsProcessor)
        setupVoting(listItem, listItemEventsProcessor)
        itemView.processingProgressBar.visibility = View.INVISIBLE
        itemView.warningIcon.visibility = View.INVISIBLE
        setupCommentTime(comment.meta)
        val longClickListener = View.OnLongClickListener {
            if (comment.isMyComment && !comment.isDeleted) {
                listItemEventsProcessor.onCommentLongClick(comment)
            }
            true
        }
        setupCommentContent(listItem, listItemEventsProcessor, longClickListener)
        itemView.setOnLongClickListener(longClickListener)
    }

    private fun setupCommentTime(meta: Meta) {
        val time = TimeEstimationFormatter.format(meta.creationTime, itemView.context)
        itemView.replyAndTimeText.text = time
    }

    private fun setupUserAvatar(author: Author, listItemEventsProcessor: ProfileCommentsModelEventProcessor) {
        val userAvatarView = itemView.ivAvatar
        userAvatarView.loadAvatar(author.avatarUrl)
        userAvatarView.setOnClickListener {
            listItemEventsProcessor.onUserClicked(author.userId)
        }
    }

    private fun setupCommentContent(
        listItem: ProfileCommentListItem,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor,
        longClickListener: View.OnLongClickListener
    ) {
        with(itemView) {
            rvCommentContent.apply {
                adapter = commentContentAdapter
                setRecycledViewPool(commentsViewPool)
                layoutManager = object: LinearLayoutManager(itemView.context){

                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }
            val comment = listItem.comment
            val body = comment.body
            val labelCommentDeleted = itemView.context.getString(R.string.comment_deleted)
            val contentList: List<Block> = body?.content ?: arrayListOf()
            val newContentList = ArrayList(contentList)
            ((body?.attachments) as? Block)?.let {
                newContentList.add(it)
            }

            if (newContentList.isEmpty() && comment.isDeleted) {
                val deleteBlock =
                    ParagraphBlock(IdUtil.generateLongId(), arrayListOf(SpanableBlock(getAuthorAndText(comment.author, labelCommentDeleted)))) as Block
                newContentList.add(deleteBlock)
            } else {
                addAuthorNameToContent(newContentList, comment)
            }
            val contentItems = newContentList
                .filter { createPostBodyItem(listItem.comment, it, listItemEventsProcessor, longClickListener) != null }
                .map {
                    createPostBodyItem(listItem.comment, it, listItemEventsProcessor, longClickListener)!!
                }
            commentContentAdapter.updateAdapter(contentItems)
        }
    }

    private fun addAuthorNameToContent(newContentList: ArrayList<Block>, comment: Comment) {
        val findBlock = newContentList.find { it is TextBlock || it is ParagraphBlock }
        val authorBlock = ParagraphBlock(null, arrayListOf(SpanableBlock(getAuthorAndText(comment.author, "")))) as Block

        if (findBlock == null) {
            newContentList.add(0, authorBlock)
        } else {
            val indexOf = newContentList.indexOf(findBlock)
            if (indexOf == 0) {
                if (findBlock is TextBlock) {
                    newContentList[0] = ParagraphBlock(null, arrayListOf(SpanableBlock(getAuthorAndText(comment.author, findBlock.content)))) as Block
                } else {
                    if (findBlock is ParagraphBlock) {
                        if (findBlock.content.isNotEmpty()) {
                            val paragraphContent = mutableListOf<ParagraphItemBlock>()

                            paragraphContent.add(TextBlock(IdUtil.generateLongId(), (comment.author.username ?: comment.author.userId)+" ", TextStyle.BOLD, null))
                            findBlock.content.forEach { block -> paragraphContent.add(block) }

                            val newParagraph = ParagraphBlock(null, paragraphContent)
                            newContentList[0] = newParagraph
                        } else {
                            newContentList[0] = authorBlock
                        }
                    }
                }
            } else {
                newContentList.add(0, authorBlock)
            }
        }
    }

    private fun getAuthorAndText(author: Author, text: String): SpannableStringBuilder {
        val result = SpannableStringBuilder()
        author.username?.let {
            val userNameInterval = result.appendText(it)
            result.setSpan(StyleSpan(Typeface.BOLD), userNameInterval)
            val colorUserName = ContextCompat.getColor(itemView.context, R.color.comment_user_name)
            result.setSpan(ForegroundColorSpan(colorUserName), userNameInterval)
        }

        result.append(SPACE)
        result.append(text)
        return result
    }

    private fun createPostBodyItem(
        comment: Comment,
        block: Block,
        listItemEventsProcessor: ProfileCommentsModelEventProcessor,
        longClickListener: View.OnLongClickListener
    ): BaseRecyclerItem? {
        return when (block) {
            is AttachmentsBlock -> {
                if (block.content.size == 1) {
                    createPostBodyItem(
                        comment,
                        block.content.single(),
                        listItemEventsProcessor,
                        longClickListener
                    ) // A single attachment is shown as embed block
                } else {
                    AttachmentBlockItem(block, listItemEventsProcessor)
                }
            }

            is ImageBlock -> CommentImageBlockItem(
                imageBlock = block,
                widgetListener = listItemEventsProcessor,
                onLongClickListener = longClickListener
            )

            is VideoBlock -> VideoBlockItem(
                videoBlock = block,
                widgetListener = listItemEventsProcessor
            )

            is WebsiteBlock -> WebSiteBlockItem(
                block,
                listItemEventsProcessor
            )

            is ParagraphBlock -> CommentParagraphBlockItem(
                block,
                listItemEventsProcessor,
                comment.contentId,
                onLongClickListener = longClickListener
            )

            is RichBlock -> CommentRichBlockItem(
                block,
                comment.contentId,
                listItemEventsProcessor
            )

            is EmbedBlock -> PostEmbedBlockItem(
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

            if(!listItem.comment.isMyComment){
                itemView.voting.upvoteButton.isEnabled = true
                itemView.voting.downvoteButton.isEnabled = true

                voting.setOnUpVoteButtonClickListener {
                    if (!listItem.comment.votes.hasUpVote) {
                        listItemEventsProcessor.onCommentUpVoteClick(listItem.comment.contentId)
                    }
                }
                voting.setOnDownVoteButtonClickListener {
                    if (!listItem.comment.votes.hasDownVote) {
                        listItemEventsProcessor.onCommentDownVoteClick(listItem.comment.contentId)
                    }
                }
            } else{
                itemView.voting.upvoteButton.isEnabled = false
                itemView.voting.downvoteButton.isEnabled = false
            }

        }
    }

    override fun release() {
        super.release()
        itemView.voting.release()
    }
}