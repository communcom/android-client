package io.golos.cyber_android.ui.screens.post_view.view.list.view_holders.comments

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.Author
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.CommentListItem
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.CommentListItemState
import io.golos.cyber_android.ui.screens.post_view.helpers.CommentTextRenderer
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.base.adapter.BaseRecyclerItem
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.shared.characters.SpecialChars
import io.golos.utils.getColorRes
import io.golos.utils.format.TimeEstimationFormatter
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.shared.widgets.post_comments.ParagraphWidgetListener
import io.golos.cyber_android.ui.shared.widgets.post_comments.voting.VotingWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.items.*
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.utils.helpers.appendSpannedText
import io.golos.utils.helpers.appendText
import io.golos.utils.helpers.setSpan
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionMetadataModel
import io.golos.domain.posts_parsing_rendering.post_metadata.TextStyle
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.*
import io.golos.utils.id.IdUtil
import io.golos.utils.helpers.SPACE
import javax.inject.Inject

@Suppress("PropertyName")
abstract class CommentViewHolderBase<T: CommentListItem>(
    parentView: ViewGroup,
    private val commentsViewPool: RecyclerView.RecycledViewPool
) : ViewHolderBase<PostPageViewModelListEventsProcessor, T>(
    parentView,
    R.layout.item_comment
) {
    private val maxStringLenToCutNeeded = 285

    @ColorInt
    private val spansColor = parentView.context.resources.getColorRes(R.color.default_clickable_span_color)

    @ColorInt
    private val moreLabelColor = parentView.context.resources.getColorRes(R.color.dark_gray)

    private val commentContentAdapter: RecyclerAdapter = RecyclerAdapter()

    @Inject
    internal lateinit var commentTextRenderer: CommentTextRenderer

    abstract val _userAvatar: ImageView

    abstract val _voting: VotingWidget

    abstract val _content: RecyclerView

    abstract val _replyAndTimeText: TextView

    abstract val _processingProgress: ProgressBar

    abstract val _warning: ImageView

    abstract val _rootView: View

    init {
        @Suppress("LeakingThis")
        inject()
    }

    @CallSuper
    override fun init(listItem: T, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        loadAvatarIcon(listItem.author.avatarUrl)
        val longClickListener = View.OnLongClickListener {
            if (!listItem.isDeleted && listItem.state != CommentListItemState.PROCESSING) {
                listItemEventsProcessor.onCommentLongClick(listItem.externalId)
            }
            true
        }
        setupCommentContent(listItem, listItemEventsProcessor, longClickListener)
        itemView.setOnLongClickListener(longClickListener)
        _replyAndTimeText.text = getReplyAndTimeText(_rootView.context.applicationContext, listItem.metadata)
        _replyAndTimeText.setOnClickListener { listItemEventsProcessor.startReplyToComment(listItem.externalId) }
        _userAvatar.setOnClickListener { listItemEventsProcessor.onUserClicked(listItem.author.userId.userId) }

        setupVoting(listItem, listItemEventsProcessor)

        setProcessingState(listItem.state)
    }

    @CallSuper
    override fun release() {
        _rootView.setOnLongClickListener(null)
        _voting.release()
    }

    abstract fun inject()

    abstract fun getParentAuthor(listItem: T): DiscussionAuthorModel?

    private fun loadAvatarIcon(avatarUrl: String?) = _userAvatar.loadAvatar(avatarUrl)

    private fun setupCommentContent(
        listItem: T,
        listItemEventsProcessor: PostPageViewModelListEventsProcessor,
        longClickListener: View.OnLongClickListener
    ) {
        _content.apply {
            adapter = commentContentAdapter
            setRecycledViewPool(commentsViewPool)
            layoutManager = object: LinearLayoutManager(itemView.context){

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        }
        val body = listItem.content
        val labelCommentDeleted = itemView.context.getString(R.string.comment_deleted)
        val contentList: List<Block> = body?.content ?: arrayListOf()
        val newContentList = ArrayList<Block>(contentList)
        ((body?.attachments) as? Block)?.let {
            newContentList.add(it)
        }

        val author = Author(listItem.author.avatarUrl, listItem.author.userId.userId, listItem.author.username)
        if (newContentList.isEmpty() && listItem.isDeleted) {
            val deleteBlock =
                ParagraphBlock(
                    IdUtil.generateLongId(),
                    arrayListOf(
                        SpanableBlock(
                            getAuthorAndText(author, labelCommentDeleted, listItemEventsProcessor)
                        )
                    )
                ) as Block
            newContentList.add(deleteBlock)
        } else {
            addAuthorNameToContent(newContentList, author, listItemEventsProcessor)
        }
        val discussionId = listItem.externalId
        val contentId = ContentIdDomain(CommunityIdDomain(""), discussionId.permlink.value, discussionId.userId)
        val contentItems = newContentList
            .filter { createPostBodyItem(contentId, it, listItemEventsProcessor, longClickListener) != null }
            .map {
                createPostBodyItem(contentId, it, listItemEventsProcessor, longClickListener)!!
            }
        commentContentAdapter.updateAdapter(contentItems)
    }

    private fun addAuthorNameToContent(newContentList: ArrayList<Block>, author: Author, paragraphWidgetListener: ParagraphWidgetListener) {
        val findBlock = newContentList.find { it is TextBlock || it is ParagraphBlock }
        val authorBlock = ParagraphBlock(
            null,
            arrayListOf(
                SpanableBlock(
                    getAuthorAndText(
                        author,
                        "",
                        paragraphWidgetListener
                    )
                )
            )
        ) as Block

        if (findBlock == null) {
            newContentList.add(0, authorBlock)
        } else {
            val indexOf = newContentList.indexOf(findBlock)
            if (indexOf == 0) {
                if (findBlock is TextBlock) {
                    newContentList[0] = ParagraphBlock(
                        null,
                        arrayListOf(
                            SpanableBlock(
                                getAuthorAndText(author, findBlock.content, paragraphWidgetListener)
                            )
                        )
                    ) as Block
                } else {
                    if (findBlock is ParagraphBlock) {
                        if (findBlock.content.isNotEmpty()) {
                            val paragraphContent = mutableListOf<ParagraphItemBlock>()

                            paragraphContent.add(
                                TextBlock(
                                    IdUtil.generateLongId(),
                                    (author.username ?: author.userId) + " ",
                                    TextStyle.BOLD,
                                    null
                                )
                            )
                            findBlock.content.forEach { block -> paragraphContent.add(block) }

                            val newParagraph =
                                ParagraphBlock(
                                    null,
                                    paragraphContent
                                )
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

    private fun createPostBodyItem(
        contentId: ContentIdDomain,
        block: Block,
        listItemEventsProcessor: PostPageViewModelListEventsProcessor,
        longClickListener: View.OnLongClickListener
    ): BaseRecyclerItem? {
        return when (block) {
            is AttachmentsBlock -> {
                if (block.content.size == 1) {
                    createPostBodyItem(
                        contentId,
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
                contentId,
                onLongClickListener = longClickListener
            )

            is RichBlock -> CommentRichBlockItem(
                block,
                contentId,
                listItemEventsProcessor
            )

            is EmbedBlock -> CommentEmbedBlockItem(
                block,
                contentId,
                listItemEventsProcessor
            )

            else -> null
        }
    }

    private fun getAuthorAndText(author: Author, text: String, paragraphWidgetListener: ParagraphWidgetListener): SpannableStringBuilder {
        val result = SpannableStringBuilder()
        author.username?.let {
            val userNameInterval = result.appendText(it)
            result.setSpan(StyleSpan(Typeface.BOLD), userNameInterval)
            val colorUserName = ContextCompat.getColor(itemView.context, R.color.comment_user_name)
            result.setSpan(object : ColorTextClickableSpan(author.userId, colorUserName){

                override fun onClick(spanData: String) {
                    super.onClick(spanData)
                    paragraphWidgetListener.onUserClicked(spanData)
                }
            }, userNameInterval)
        }

        result.append(SPACE)
        result.append(text)
        return result
    }

    private fun setupVoting(listItem: T, eventsProcessor: PostPageViewModelListEventsProcessor) {
        _voting.setVoteBalance(listItem.voteBalance)
        _voting.setUpVoteButtonSelected(listItem.isUpVoteActive)
        _voting.setDownVoteButtonSelected(listItem.isDownVoteActive)

        _voting.setOnUpVoteButtonClickListener { eventsProcessor.onCommentUpVoteClick(listItem.externalId) }
        _voting.setOnDownVoteButtonClickListener { eventsProcessor.onCommentDownVoteClick(listItem.externalId) }
    }

    private fun getReplyAndTimeText(context: Context, metadata: DiscussionMetadataModel): SpannableStringBuilder {
        val result = SpannableStringBuilder()

        // Reply label
        val replySpan = object: ColorTextClickableSpan("", spansColor) {
            override fun onClick(spanData: String) {
                // Reply
            }
        }
        result.appendSpannedText(context.resources.getString(R.string.reply), replySpan)

        // Time
        val time = TimeEstimationFormatter.format(metadata.time, context)
        result.append(" ${SpecialChars.BULLET} ")
        result.append(time)

        return result
    }

    private fun setProcessingState(state: CommentListItemState) {
        _processingProgress.visibility = if(state == CommentListItemState.PROCESSING) View.VISIBLE else View.INVISIBLE
        _warning.visibility = if(state == CommentListItemState.ERROR) View.VISIBLE else View.INVISIBLE
    }
}
